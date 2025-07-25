package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.dataPlatform.service.DataPlatformService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.product.vo.CreateOrderProductUseCaseVo;
import kr.hhplus.be.server.domain.product.vo.CreateOrderUseCaseVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import javax.naming.InsufficientResourcesException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
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
        CreateOrderUseCaseVo createOrderUseCaseVo = CreateOrderUseCaseVo.of( userId, List.of(productVo));

        when(productService.checkStock(anyLong(), anyInt())).thenReturn(true);
        when(balanceService.hasSufficientBalance(anyLong(), anyInt())).thenReturn(true);
        when(balanceService.useBalance(anyLong(), anyInt())).thenReturn(any());
        when(orderService.save(any())).thenReturn(any(Order.class));
        doNothing().when(dataPlatformService).sendOrderData(any(Order.class));

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
        CreateOrderUseCaseVo createOrderUseCaseVo = CreateOrderUseCaseVo.of(userId, List.of(productVo));

        when(productService.checkStock(anyLong(), anyInt())).thenReturn(false);

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
        CreateOrderUseCaseVo createOrderUseCaseVo = CreateOrderUseCaseVo.of(userId, List.of(productVo));

        when(productService.checkStock(anyLong(), anyInt())).thenReturn(true);
        when(balanceService.hasSufficientBalance(anyLong(), anyInt())).thenReturn(false);

        // When, Then
        assertThatThrownBy(() -> orderFacade.createOrder(createOrderUseCaseVo))
                .isInstanceOf(InsufficientResourcesException.class);
    }
}
