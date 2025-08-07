package kr.hhplus.be.server.domain.order.facade;

import kr.hhplus.be.server.DatabaseClean;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
import kr.hhplus.be.server.domain.coupon.command.CreateIssuedCouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.AssertionsForClassTypes.*;

@SpringBootTest
@Testcontainers
class OrderFacadeIntegrationTest {
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
    private IssuedCouponRepository issuedCouponRepository;

    @Autowired
    private DatabaseClean dataBaseClean;

    @BeforeEach
    void setUp() {
        dataBaseClean.execute();
    }

    @DisplayName("[성공] 쿠폰 없이 주문 성공")
    @Test
    void 쿠폰_없이_주문_성공() {
        // Given
        Long userId = 1L;
        Integer initialBalance = 100000;
        Balance balance = Balance.create(userId, initialBalance);
        this.balanceRepository.save(balance);

        Integer productStock = 100;
        Integer productPrice = 10000;
        Product product = productRepository.save(Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        ));
        this.productRepository.save(product);

        Integer buyProductStock = 2;
        Integer useAmount = productPrice * buyProductStock;
        OrderCommand orderCommand = new OrderCommand(
                List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                new OrderPaymentCommand(useAmount, 0, useAmount),
                new UseBalanceCommand(userId, useAmount),
                List.of()
        );

        // When
        orderFacade.createOrder(userId, orderCommand);

        // Then
        // 주문 데이터가 정상적으로 생성되었는지 검증
        Order order = orderRepository.findAll().get(0);
        assertThat(order.getUserId()).isEqualTo(userId);
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderId(order.getId());
        assertThat(orderProducts.get(0).getProductId()).isEqualTo(product.getId());
        assertThat(orderProducts.get(0).getQuantity()).isEqualTo(buyProductStock);

        // 재고 차감이 정상적으로 이루어졌는지 검증
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(productStock - buyProductStock);

        // 잔액 사용이 정상적으로 이루어졌는지 검증
        Balance updatedBalance = balanceRepository.findById(userId).orElseThrow();
        assertThat(updatedBalance.getAmount()).isEqualTo(initialBalance - useAmount);
    }

    @DisplayName("[성공] 쿠폰 사용하여 주문 성공")
    @Test
    void 쿠폰_사용하여_주문_성공() {
        // Given
        Long userId = 1L;
        Integer initialBalance = 100000;
        Balance balance = Balance.create(userId, initialBalance);
        this.balanceRepository.save(balance);

        Integer productStock = 100;
        Integer productPrice = 10000;
        Product product = productRepository.save(Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        ));
        this.productRepository.save(product);

        Integer discountAmount = 5000;
        IssuedCoupon issuedCoupon = IssuedCoupon.create(userId, 1L, LocalDateTime.now().plusDays(30));
        this.issuedCouponRepository.save(issuedCoupon);

        Integer buyProductStock = 2;
        Integer orderAmount = (productPrice * buyProductStock);
        Integer useAmount = orderAmount - discountAmount;
        OrderCommand orderCommand = new OrderCommand(
                List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                new OrderPaymentCommand(orderAmount, discountAmount, useAmount),
                new UseBalanceCommand(userId, useAmount),
                List.of(new UseCouponCommand(userId, issuedCoupon.getId()))
        );

        // When
        orderFacade.createOrder(userId, orderCommand);

        // Then
        // 주문 데이터가 정상적으로 생성되었는지 검증
        Order order = orderRepository.findAll().get(0);
        assertThat(order.getUserId()).isEqualTo(userId);
        List<OrderProduct> orderProducts = orderProductRepository.findAllByOrderId(order.getId());
        assertThat(orderProducts.get(0).getProductId()).isEqualTo(product.getId());
        assertThat(orderProducts.get(0).getQuantity()).isEqualTo(buyProductStock);

        // 재고 차감이 정상적으로 이루어졌는지 검증
        Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();
        assertThat(updatedProduct.getStock()).isEqualTo(productStock - buyProductStock);

        // 잔액 사용이 정상적으로 이루어졌는지 검증
        Balance updatedBalance = balanceRepository.findById(userId).orElseThrow();
        assertThat(updatedBalance.getAmount()).isEqualTo(initialBalance - useAmount);

        // 쿠폰 사용이 정상적으로 이루어졌는지 검증
        IssuedCoupon updatedIssuedCoupon = issuedCouponRepository.findById(issuedCoupon.getId()).orElseThrow();
        assertThat(updatedIssuedCoupon.getUsedAt()).isNotNull();
    }

    @DisplayName("[실패] 재고 부족으로 주문 실패")
    @Test
    void 재고_부족으로_주문_실패() {
        // Given
        Long userId = 1L;
        Integer initialBalance = 100000;
        Balance balance = Balance.create(userId, initialBalance);
        this.balanceRepository.save(balance);

        Integer productStock = 0; // 재고가 없는 상태
        Integer productPrice = 10000;
        Product product = productRepository.save(Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        ));
        this.productRepository.save(product);

        Integer buyProductStock = 1;
        Integer useAmount = productPrice * buyProductStock;
        OrderCommand orderCommand = new OrderCommand(
                List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                new OrderPaymentCommand(useAmount, 0, useAmount),
                new UseBalanceCommand(userId, useAmount),
                List.of()
        );

        // When & Then
        assertThatThrownBy(() -> orderFacade.createOrder(userId, orderCommand))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[실패] 잔액 부족으로 주문 실패")
    @Test
    void 잔액_부족으로_주문_실패() {
        // Given
        Long userId = 1L;
        Integer initialBalance = 10000; // 잔액이 부족한 상태
        Balance balance = Balance.create(userId, initialBalance);
        this.balanceRepository.save(balance);

        Integer productStock = 100;
        Integer productPrice = 20000; // 잔액보다 높은 가격
        Product product = productRepository.save(Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        ));
        this.productRepository.save(product);

        Integer buyProductStock = 1;
        Integer useAmount = productPrice * buyProductStock;
        OrderCommand orderCommand = new OrderCommand(
                List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                new OrderPaymentCommand(useAmount, 0, useAmount),
                new UseBalanceCommand(userId, useAmount),
                List.of()
        );

        // When & Then
        assertThatThrownBy(() -> orderFacade.createOrder(userId, orderCommand))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("[실패] 쿠폰 사용 기간 만료로 주문 실패")
    @Test
    void 쿠폰_사용_기간_만료로_주문_실패() {
        // Given
        Long userId = 1L;
        Integer initialBalance = 100000;
        Balance balance = Balance.create(userId, initialBalance);
        this.balanceRepository.save(balance);

        Integer productStock = 100;
        Integer productPrice = 10000;
        Product product = Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        );
        productRepository.save(product);

        // 쿠폰 사용 기간이 지난 쿠폰 생성
        Integer discountAmount = 5000;
        IssuedCoupon issuedCoupon = IssuedCoupon.create(userId, 1L, LocalDateTime.now().minusDays(1));
        this.issuedCouponRepository.save(issuedCoupon);

        Integer buyProductStock = 1;
        Integer orderAmount = productPrice;
        Integer useAmount = orderAmount - discountAmount; // 할인 금액을 고려한 사용 금액
        OrderCommand orderCommand = new OrderCommand(
                List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                new OrderPaymentCommand(orderAmount, discountAmount, useAmount),
                new UseBalanceCommand(userId, useAmount),
                List.of(new UseCouponCommand(userId, issuedCoupon.getId()))
        );

        // When & Then
        assertThatThrownBy(() -> orderFacade.createOrder(userId, orderCommand))
                .isInstanceOf(IllegalStateException.class);
    }

    /**
     * 동시성 문제가 일어나는 곳은 하나의 리소스를 다른 유저들이 동시에 접근이 가능한 경우로 판단.
     * 주문 기능에서 동시성 이슈가 생길 가능성이 높은 곳은 재고 감소라고 생각하여, 재고 감소에 대해서만 동시성 이슈를 판별한다.
     */
    @DisplayName("[성공] 5명이 동시에 주문을 생성할 때, 각각의 주문이 정상적으로 처리되어야 한다.")
    @Test
    void 성공_동시성_테스트() throws InterruptedException {
        // Given
        // 각 사용자의 초기 잔액과 쿠폰 지급
        Integer initialBalance = 100000;
        Integer discountAmount = 1000;
        List<Long> userIds = List.of(1L, 2L, 3L, 4L, 5L);
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
        int threads = 5; // 5명의 사용자 동시 진행
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
        Integer expectedOrderCount = 5; // 5명의 사용자가 주문을 생성했으므로
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
        List<Long> userIds = List.of(1L, 2L, 3L, 4L, 5L);
        for (Long userId : userIds) {
            Balance balance = Balance.create(userId, initialBalance);
            this.balanceRepository.save(balance);
            this.issuedCouponService.createIssuedCoupon(
                    new CreateIssuedCouponCommand(userId, 1L, 7)
            );
        }

        Integer productStock = 3; // 제한된 재고
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
        int threads = 5; // 재고보다 많은 수의 스레드
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
        List<Long> userIds = List.of(1L, 2L, 3L, 4L, 5L);
        for (Long userId : userIds) {
            Balance balance = Balance.create(userId, initialBalance);
            if (userId == 5L) {
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
        int threads = 5; // 5명의 사용자 동시 진행
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
        Integer expectedOrderCount = 4; // 5명 중 1명(잔액 부족)이 실패하므로 4개의 주문이 성공해야 함
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
        List<Long> userIds = List.of(1L, 2L, 3L, 4L, 5L);
        for (Long userId : userIds) {
            Balance balance = Balance.create(userId, initialBalance);
            this.balanceRepository.save(balance);

            if (userId == 5L) {
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
        int threads = 5; // 5명의 사용자 동시 진행
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
        Integer expectedOrderCount = 4; // 5명 중 1명(잘못된 쿠폰 사용)이 실패하므로 4개의 주문이 성공해야 함
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
