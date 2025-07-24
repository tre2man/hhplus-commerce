package kr.hhplus.be.server.domain.order.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.naming.InsufficientResourcesException;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.Mockito.when;

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

    @BeforeEach
    void setUp() {
        orderFacade = new OrderFacade(
                orderService,
                productService,
                balanceService,
                dataPlatformService
        );
    }

    @Test
    @DisplayName("[성공] 주문 생성")
    void 주문_생성_성공() {
        // Given
        Long userId = 1L;

        CreateOrderProductUseCaseVo productVo = CreateOrderProductUseCaseVo.of(1L, 2);
        CreateOrderUseCaseVo createOrderUseCaseVo = CreateOrderUseCaseVo.of(userId, productVo,

        when(productService.checkStock).thenReturn(true);
        when(balanceService.hasSufficientBalance).thenReturn(true);
        when(balanceService.useBalance).thenReturn(true);
        when(orderService.save).thenReturn(true);
        when(dataPlatformService.sendOrderData).thenReturn(true);

        // When
        Order order = orderFacade.createOrder(createOrderUseCaseVo);

        // Then
        assertThat(order).isNotNull();
    }

    @Test
    @DisplayName("[실패] 주문 생성 실패 - 재고 부족")
    void 주문_생성_실패_재고_부족() {
        // Given
        Long userId = 1L;
        Long issuedCouponId = 1L;

        CreateOrderProductUseCaseVo productVo = CreateOrderProductUseCaseVo.of(1L, 2);
        CreateOrderUseCaseVo createOrderUseCaseVo = CreateOrderUseCaseVo.of(userId, productVo, issuedCouponId);

        when(productService.checkStock).thenReturn(false);

        // When, Then
        assertThatThrownBy(() -> orderFacade.createOrder(createOrderUseCaseVo))
                .isInstanceOf(InsufficientResourcesException.class);
    }

    @Test
    @DisplayName("[실패] 주문 생성 실패 - 잔액 부족")
    void 주문_생성_실패_잔액_부족() {
        // Given
        Long userId = 1L;
        Long issuedCouponId = 1L;

        CreateOrderProductUseCaseVo productVo = CreateOrderProductUseCaseVo.of(1L, 2);
        CreateOrderUseCaseVo createOrderUseCaseVo = CreateOrderUseCaseVo.of(userId, productVo);

        when(productService.checkStock).thenReturn(true);
        when(balanceService.hasSufficientBalance).thenReturn(false);

        // When, Then
        assertThatThrownBy(() -> orderFacade.createOrder(createOrderUseCaseVo))
                .isInstanceOf(InsufficientResourcesException.class);
    }
}
