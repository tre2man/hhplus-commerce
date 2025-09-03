package kr.hhplus.be.server.domain.order.facade;

import kr.hhplus.be.server.DatabaseClean;
import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.repository.BalanceRepository;
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
                userId,
                List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                new OrderPaymentCommand(useAmount, 0, useAmount),
                new UseBalanceCommand(userId, useAmount),
                List.of()
        );

        // When
        orderFacade.createOrder(orderCommand);

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
        Product product = Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        );
        this.productRepository.save(product);

        Integer discountAmount = 5000;
        IssuedCoupon issuedCoupon = IssuedCoupon.create(userId, 1L, LocalDateTime.now().plusDays(30));
        this.issuedCouponRepository.save(issuedCoupon);

        Integer buyProductStock = 2;
        Integer orderAmount = (productPrice * buyProductStock);
        Integer useAmount = orderAmount - discountAmount;
        OrderCommand orderCommand = new OrderCommand(
                userId,
                List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                new OrderPaymentCommand(orderAmount, discountAmount, useAmount),
                new UseBalanceCommand(userId, useAmount),
                List.of(new UseCouponCommand(userId, issuedCoupon.getId()))
        );

        // When
        orderFacade.createOrder(orderCommand);

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
        Product product = Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        );
        this.productRepository.save(product);

        Integer buyProductStock = 1;
        Integer useAmount = productPrice * buyProductStock;
        OrderCommand orderCommand = new OrderCommand(
                userId,
                List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                new OrderPaymentCommand(useAmount, 0, useAmount),
                new UseBalanceCommand(userId, useAmount),
                List.of()
        );

        // When & Then
        assertThatThrownBy(() -> orderFacade.createOrder(orderCommand))
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
        Product product = Product.create(
                "테스트 상품",
                productStock,
                productPrice,
                "테스트 설명"
        );
        this.productRepository.save(product);

        Integer buyProductStock = 1;
        Integer useAmount = productPrice * buyProductStock;
        OrderCommand orderCommand = new OrderCommand(
                userId,
                List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                new OrderPaymentCommand(useAmount, 0, useAmount),
                new UseBalanceCommand(userId, useAmount),
                List.of()
        );

        // When & Then
        assertThatThrownBy(() -> orderFacade.createOrder(orderCommand))
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
                userId,
                List.of(new OrderProductCommand(product.getId(), buyProductStock)),
                new OrderPaymentCommand(orderAmount, discountAmount, useAmount),
                new UseBalanceCommand(userId, useAmount),
                List.of(new UseCouponCommand(userId, issuedCoupon.getId()))
        );

        // When & Then
        assertThatThrownBy(() -> orderFacade.createOrder(orderCommand))
                .isInstanceOf(IllegalStateException.class);
    }
}
