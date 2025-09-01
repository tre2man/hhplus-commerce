package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.balance.entity.Balance;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.coupon.service.IssuedCouponService;
import kr.hhplus.be.server.domain.dataPlatform.service.DataPlatformService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderProduct;
import kr.hhplus.be.server.domain.order.vo.CreateOrderVo;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.product.vo.CreateOrderProductUseCaseVo;
import kr.hhplus.be.server.domain.product.vo.CreateOrderUseCaseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderFacadeTest {
    private OrderFacade orderFacade;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    @Mock
    private BalanceService balanceService;

    @Mock
    private DataPlatformService dataPlatformService;

    @Mock
    private IssuedCouponService issuedCouponService;

    @BeforeEach
    void setUp() {
        orderFacade = new OrderFacade(
                orderService,
                productService,
                balanceService,
                dataPlatformService,
                issuedCouponService
        );
    }

    @Test
    @DisplayName("[성공] 주문 생성")
    void 주문_생성_성공() {
        // Given
        Long userId = 1L;
        CreateOrderProductUseCaseVo productVo = CreateOrderProductUseCaseVo.of(1L, 1);
        CreateOrderUseCaseVo createOrderUseCaseVo = CreateOrderUseCaseVo.of(userId, List.of(productVo), Optional.empty());

        Product mockProduct = Product.create("상품명", 10, 1000, "설명");
        Balance mockBalance = mock(Balance.class);

        Order realOrder = Order.create(userId, 2000, 2000, null);
        realOrder.addOrderProduct(
                OrderProduct.create(realOrder, mockProduct.getId(), mockProduct.getPrice(), productVo.getQuantity())
        );

        when(productService.checkStock(anyLong(), anyInt())).thenReturn(true);
        when(balanceService.useBalance(anyLong(), anyInt())).thenReturn(mockBalance);
        when(orderService.createOrder(any(CreateOrderVo.class))).thenReturn(realOrder);
        doNothing().when(dataPlatformService).sendOrderData();

        // When
        Order order = orderFacade.createOrder(createOrderUseCaseVo);

        // Then
        assertThat(order).isNotNull();
        assertThat(order.getOrderProducts()).isNotNull();
    }

    @Test
    @DisplayName("[성공] 쿠폰을 사용한 주문 생성")
    void 쿠폰_사용_주문_생성_성공() {
        // Given
        Long userId = 1L;
        CreateOrderProductUseCaseVo productVo = CreateOrderProductUseCaseVo.of(1L, 2);
        CreateOrderUseCaseVo createOrderUseCaseVo = CreateOrderUseCaseVo.of(userId, List.of(productVo), Optional.empty());

        Product mockProduct = Product.create("상품명", 10, 1000, "설명");
        Balance mockBalance = mock(Balance.class);

        Order realOrder = Order.create(userId, 2000, 2000, null);
        realOrder.addOrderProduct(
                OrderProduct.create(realOrder, mockProduct.getId(), mockProduct.getPrice(), productVo.getQuantity())
        );

        when(productService.checkStock(anyLong(), anyInt())).thenReturn(true);
        when(balanceService.useBalance(anyLong(), anyInt())).thenReturn(mockBalance);
        when(orderService.createOrder(any(CreateOrderVo.class))).thenReturn(realOrder);
        doNothing().when(dataPlatformService).sendOrderData();

        // When
        Order order = orderFacade.createOrder(createOrderUseCaseVo);

        // Then
        assertThat(order).isNotNull();
        assertThat(order.getOrderProducts()).isNotNull();
    }

    @Test
    @DisplayName("[실패] 주문 생성 실패 - 재고 부족")
    void 주문_생성_실패_재고_부족() {
        // Given
        Long userId = 1L;

        CreateOrderProductUseCaseVo productVo = CreateOrderProductUseCaseVo.of(1L, 2);
        CreateOrderUseCaseVo createOrderUseCaseVo = CreateOrderUseCaseVo.of(userId, List.of(productVo), Optional.empty());

        when(productService.checkStock(anyLong(), anyInt())).thenReturn(false);

        // When, Then
        assertThatThrownBy(() -> orderFacade.createOrder(createOrderUseCaseVo))
                .isInstanceOf(IllegalArgumentException.class);
    }


    @Test
    @DisplayName("[실패] 주문 생성 실패 - 잔액 부족")
    void 주문_생성_실패_잔액_부족() {
        // Given
        Long userId = 1L;
        CreateOrderProductUseCaseVo productVo = CreateOrderProductUseCaseVo.of(1L, 2);
        CreateOrderUseCaseVo createOrderUseCaseVo = CreateOrderUseCaseVo.of(userId, List.of(productVo), Optional.empty());

        // 필수 mock
        Product product = Product.create("상품명", 10, 1000, "desc");

        when(productService.checkStock(anyLong(), anyInt())).thenReturn(true);
        when(balanceService.useBalance(anyLong(), anyInt())).thenThrow(new IllegalArgumentException());

        // When, Then
        assertThatThrownBy(() -> orderFacade.createOrder(createOrderUseCaseVo))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
