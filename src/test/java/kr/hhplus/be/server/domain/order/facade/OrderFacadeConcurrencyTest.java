package kr.hhplus.be.server.domain.order.facade;

import kr.hhplus.be.server.DatabaseClean;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.coupon.command.CreateIssuedCouponCommand;
import kr.hhplus.be.server.domain.coupon.service.IssuedCouponService;
import kr.hhplus.be.server.domain.order.command.*;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderProduct;
import kr.hhplus.be.server.domain.order.repository.OrderProductRepository;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
class OrderFacadeConcurrencyTest {
    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BalanceRepository balanceRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    @Autowired
    private IssuedCouponService issuedCouponService;

    @Autowired
    private DatabaseClean dataBaseClean;

    @BeforeEach
    void setUp() {
        dataBaseClean.execute();
    }


    /**
     * 동시성 문제가 일어나는 곳은 하나의 리소스를 다른 유저들이 동시에 접근이 가능한 경우로 판단.
     * 주문 기능에서 동시성 이슈가 생길 가능성이 높은 곳은 재고 감소라고 생각하여, 재고 감소에 대해서만 동시성 이슈를 판별한다.
     */
    @DisplayName("[성공] 2명이 동시에 주문을 생성할 때, 각각의 주문이 정상적으로 처리되어야 한다.")
    @Test
    void 성공_동시성_테스트() throws InterruptedException {
        // Given
        // 각 사용자의 초기 잔액과 쿠폰 지급
        Integer initialBalance = 100000;
        Integer discountAmount = 1000;
        List<Long> userIds = List.of(1L, 2L);
        for (Long userId : userIds) {
            Balance balance = Balance.create(userId, initialBalance);
            this.balanceRepository.save(balance);
            this.issuedCouponService.createIssuedCoupon(
                    new CreateIssuedCouponCommand(userId, 1L, 7)
            );
        }

        Integer productStock = 100;
        Integer productPrice = 10000;
        Product product = Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        );
        productRepository.save(product);

        Integer buyProductStock = 2;
        Integer orderAmount = productPrice * buyProductStock;
        Integer useAmount = orderAmount - discountAmount;

        // When
        int threads = 2; // 2명의 사용자 동시 진행
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            Long currentUserId = i + 1L;
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    OrderCommand orderCommand = new OrderCommand(
                            List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                            new OrderPaymentCommand(orderAmount, discountAmount, useAmount),
                            new UseBalanceCommand(currentUserId, useAmount),
                            // 최초 쿠폰 생성 시 issuedCouponId = userId 로 같다고 상정
                            List.of(new UseCouponCommand(currentUserId, currentUserId))
                    );

                    orderFacade.createOrder(currentUserId, orderCommand);
                    successCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace(); // 예외 발생 시 로그 출력
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // Then
        // 상품 재고 확인
        Integer expectedOrderCount = threads; // 5명의 사용자가 주문을 생성했으므로
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(productStock - (buyProductStock * expectedOrderCount));
    }

    /**
     * 동시성 문제가 일어나는 곳은 하나의 리소스를 다른 유저들이 동시에 접근이 가능한 경우로 판단.
     * 주문 실패 시 확인해야 할 곳은 주문 정보, 잔액 정보, 상품 재고 정보, 쿠폰 정보이다.
     */
    @DisplayName("[성공] 동시에 같은 상품을 주문할 때 재고 부족으로 일부 주문이 실패해야 한다.")
    @Test
    void 성공_상품_재고_부족_동시성_테스트() throws InterruptedException {
        // Given
        // 각 사용자의 초기 잔액과 쿠폰 지급
        Integer initialBalance = 100000;
        Integer discountAmount = 1000;
        List<Long> userIds = List.of(1L, 2L);
        for (Long userId : userIds) {
            Balance balance = Balance.create(userId, initialBalance);
            this.balanceRepository.save(balance);
            this.issuedCouponService.createIssuedCoupon(
                    new CreateIssuedCouponCommand(userId, 1L, 7)
            );
        }

        Integer productStock = 1; // 제한된 재고
        Integer productPrice = 10000;
        Product product = Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        );
        productRepository.save(product);

        Integer buyProductStock = 1;
        Integer orderAmount = productPrice * buyProductStock;
        Integer useAmount = orderAmount - discountAmount;

        // When
        int threads = 2; // 재고보다 많은 수의 스레드
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            Long currentUserId = i + 1L; // 1부터 5까지의 사용자 ID
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    OrderCommand orderCommand = new OrderCommand(
                            List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                            new OrderPaymentCommand(orderAmount, discountAmount, useAmount),
                            new UseBalanceCommand(currentUserId, useAmount),
                            List.of()
                    );

                    orderFacade.createOrder(currentUserId, orderCommand);
                    successCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // Then
        // 주문 정보 확인
        Integer expectedOrderCount = productStock;
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList.size()).isEqualTo(expectedOrderCount);

        List<Long> orderSuccessUserIdList = orderList.stream()
                .map(Order::getUserId)
                .sorted()
                .toList();
        List<Long> orderFailedUserIdList = userIds.stream()
                .filter(userId -> !orderSuccessUserIdList.contains(userId))
                .sorted()
                .toList();

        // 성공한 주문자의 정보 확인
        for (Long successOrderUserId : orderSuccessUserIdList) {
            // 주문 정보
            Long orderId = orderList.stream()
                    .filter(order -> order.getUserId().equals(successOrderUserId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."))
                    .getId();
            List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderId(orderId);
            assertThat(orderProducts.size()).isEqualTo(1);
            assertThat(orderProducts.get(0).getProductId()).isEqualTo(product.getId());
            assertThat(orderProducts.get(0).getQuantity()).isEqualTo(buyProductStock);

            // 잔액 정보
            Balance updatedBalance = balanceRepository.findById(successOrderUserId).orElseThrow();
            assertThat(updatedBalance.getAmount()).isEqualTo(initialBalance - useAmount);
        }

        // 실패한 주문자의 정보 확인
        for (Long failedOrderUserId : orderFailedUserIdList) {
            // 주문 정보가 없어야 함
            Optional<Long> failedOrderId = orderList.stream()
                    .filter(order -> order.getUserId().equals(failedOrderUserId))
                    .map(Order::getId)
                    .findFirst();
            assertThat(failedOrderId).isEmpty();

            // 잔액 정보
            Balance updatedBalance = balanceRepository.findById(failedOrderUserId).orElseThrow();
            assertThat(updatedBalance.getAmount()).isEqualTo(initialBalance);
        }

        // 상품 재고 확인
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isZero();
    }

    /**
     * 동시성 문제가 일어나는 곳은 하나의 리소스를 다른 유저들이 동시에 접근이 가능한 경우로 판단.
     * 주문 실패 시 확인해야 할 곳은 주문 정보, 잔액 정보, 상품 재고 정보, 쿠폰 정보이다.
     */
    @DisplayName("[성공] 동시에 같은 상품을 주문할 때 일부 인원의 잔고 부족으로 해당 주문이 실패해야 한다.")
    @Test
    void 실패_잔고부족_동시성() throws InterruptedException {
        // Given
        // 각 사용자의 초기 잔액과 쿠폰 지급
        Integer initialBalance = 100000;
        Integer initialBalanceForFailure = 500;
        Integer discountAmount = 1000;
        List<Long> userIds = List.of(1L, 2L);
        for (Long userId : userIds) {
            Balance balance = Balance.create(userId, initialBalance);
            if (userId == 2L) {
                balance.setAmount(initialBalanceForFailure);
            }
            this.balanceRepository.save(balance);
            this.issuedCouponService.createIssuedCoupon(
                    new CreateIssuedCouponCommand(userId, 1L, 7)
            );
        }

        Integer productStock = 100;
        Integer productPrice = 10000;
        Product product = productRepository.save(Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        ));
        Integer buyProductStock = 2;
        Integer orderAmount = productPrice * buyProductStock;
        Integer useAmount = orderAmount - discountAmount;

        // When
        int threads = 2; // 2명의 사용자 동시 진행
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            Long currentUserId = i + 1L;
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    OrderCommand orderCommand = new OrderCommand(
                            List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                            new OrderPaymentCommand(orderAmount, discountAmount, useAmount),
                            new UseBalanceCommand(currentUserId, useAmount),
                            // 최초 쿠폰 생성 시 issuedCouponId = userId 로 같다고 상정
                            List.of(new UseCouponCommand(currentUserId, currentUserId))
                    );

                    orderFacade.createOrder(currentUserId, orderCommand);
                    successCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace(); // 예외 발생 시 로그 출력
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // Then
        Integer expectedOrderCount = 1; // 2명 중 1명(잔액 부족)이 실패하므로 1개의 주문이 성공해야 함
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList.size()).isEqualTo(expectedOrderCount);

        List<Long> orderSuccessUserIdList = orderList.stream()
                .map(Order::getUserId)
                .sorted()
                .toList();
        List<Long> orderFailedUserIdList = userIds.stream()
                .filter(userId -> !orderSuccessUserIdList.contains(userId))
                .sorted()
                .toList();

        // 성공한 주문자의 정보 확인
        for (Long successOrderUserId : orderSuccessUserIdList) {
            // 주문 정보
            Long orderId = orderList.stream()
                    .filter(order -> order.getUserId().equals(successOrderUserId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."))
                    .getId();
            List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderId(orderId);
            assertThat(orderProducts.size()).isEqualTo(1);
            assertThat(orderProducts.get(0).getProductId()).isEqualTo(product.getId());
            assertThat(orderProducts.get(0).getQuantity()).isEqualTo(buyProductStock);

            // 잔액 정보
            Balance updatedBalance = balanceRepository.findById(successOrderUserId).orElseThrow();
            assertThat(updatedBalance.getAmount()).isEqualTo(initialBalance - useAmount);
        }

        // 실패한 주문자의 정보 확인
        for (Long failedOrderUserId : orderFailedUserIdList) {
            // 주문 정보가 없어야 함
            Optional<Long> failedOrderId = orderList.stream()
                    .filter(order -> order.getUserId().equals(failedOrderUserId))
                    .map(Order::getId)
                    .findFirst();
            assertThat(failedOrderId).isEmpty();

            // 잔액 정보
            Balance updatedBalance = balanceRepository.findById(failedOrderUserId).orElseThrow();
            assertThat(updatedBalance.getAmount()).isEqualTo(initialBalanceForFailure);
        }

        // 상품 재고 확인
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(productStock - (buyProductStock * expectedOrderCount));
    }


    /**
     * 동시성 문제가 일어나는 곳은 하나의 리소스를 다른 유저들이 동시에 접근이 가능한 경우로 판단.
     * 주문 실패 시 확인해야 할 곳은 주문 정보, 잔액 정보, 상품 재고 정보, 쿠폰 정보이다.
     */
    @DisplayName("[성공] 동시에 같은 상품을 주문할 때 일부 인원의 쿠폰 유효성 문제로 인해 해당 주문이 실패해야 한다.")
    @Test
    void 실패_잘못된쿠폰_동시성() throws InterruptedException {
        // Given
        // 각 사용자의 초기 잔액과 쿠폰 지급
        Integer initialBalance = 100000;
        Integer discountAmount = 1000;
        List<Long> userIds = List.of(1L, 2L);
        for (Long userId : userIds) {
            Balance balance = Balance.create(userId, initialBalance);
            this.balanceRepository.save(balance);
            if (userId == 2L) {
                // 5번 사용자에게는 잘못된 쿠폰 지급
                this.issuedCouponService.createIssuedCoupon(
                        new CreateIssuedCouponCommand(userId, 1L, -1)
                );
            } else {
                // 나머지 사용자에게는 정상적인 쿠폰 지급
                this.issuedCouponService.createIssuedCoupon(
                        new CreateIssuedCouponCommand(userId, 1L, 7)
                );
            }
        }

        Integer productStock = 100;
        Integer productPrice = 10000;
        Product product = productRepository.save(Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        ));
        Integer buyProductStock = 2;
        Integer orderAmount = productPrice * buyProductStock;
        Integer useAmount = orderAmount - discountAmount;

        // When
        int threads = 2; // 2명의 사용자 동시 진행
        CountDownLatch readyLatch = new CountDownLatch(threads);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threads);
        ExecutorService executor = Executors.newFixedThreadPool(threads);
        AtomicInteger successCount = new AtomicInteger(0);

        for (int i = 0; i < threads; i++) {
            Long currentUserId = i + 1L;
            executor.submit(() -> {
                try {
                    readyLatch.countDown();
                    startLatch.await();

                    OrderCommand orderCommand = new OrderCommand(
                            List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                            new OrderPaymentCommand(orderAmount, discountAmount, useAmount),
                            new UseBalanceCommand(currentUserId, useAmount),
                            // 최초 쿠폰 생성 시 issuedCouponId = userId 로 같다고 상정
                            List.of(new UseCouponCommand(currentUserId, currentUserId))
                    );

                    orderFacade.createOrder(currentUserId, orderCommand);
                    successCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    e.printStackTrace(); // 예외 발생 시 로그 출력
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executor.shutdown();

        // Then
        Integer expectedOrderCount = 1; // 2명 중 1명(잘못된 쿠폰 사용)이 실패하므로 1개의 주문이 성공해야 함
        List<Order> orderList = orderRepository.findAll();
        assertThat(orderList.size()).isEqualTo(expectedOrderCount);

        List<Long> orderSuccessUserIdList = orderList.stream()
                .map(Order::getUserId)
                .sorted()
                .toList();
        List<Long> orderFailedUserIdList = userIds.stream()
                .filter(userId -> !orderSuccessUserIdList.contains(userId))
                .sorted()
                .toList();

        // 성공한 주문자의 정보 확인
        for (Long successOrderUserId : orderSuccessUserIdList) {
            // 주문 정보
            Long orderId = orderList.stream()
                    .filter(order -> order.getUserId().equals(successOrderUserId))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."))
                    .getId();
            List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderId(orderId);
            assertThat(orderProducts.size()).isEqualTo(1);
            assertThat(orderProducts.get(0).getProductId()).isEqualTo(product.getId());
            assertThat(orderProducts.get(0).getQuantity()).isEqualTo(buyProductStock);

            // 잔액 정보
            Balance updatedBalance = balanceRepository.findById(successOrderUserId).orElseThrow();
            assertThat(updatedBalance.getAmount()).isEqualTo(initialBalance - useAmount);
        }

        // 실패한 주문자의 정보 확인
        for (Long failedOrderUserId : orderFailedUserIdList) {
            // 주문 정보가 없어야 함
            Optional<Long> failedOrderId = orderList.stream()
                    .filter(order -> order.getUserId().equals(failedOrderUserId))
                    .map(Order::getId)
                    .findFirst();
            assertThat(failedOrderId).isEmpty();

            // 잔액 정보
            Balance updatedBalance = balanceRepository.findById(failedOrderUserId).orElseThrow();
            assertThat(updatedBalance.getAmount()).isEqualTo(initialBalance);
        }

        // 상품 재고 확인
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(productStock - (buyProductStock * expectedOrderCount));
    }
}
