package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.coupon.service.IssuedCouponService;
import kr.hhplus.be.server.domain.dataPlatform.service.DataPlatformService;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.command.OrderPaymentCommand;
import kr.hhplus.be.server.domain.order.command.OrderProductCommand;
import kr.hhplus.be.server.domain.order.command.UseBalanceCommand;
import kr.hhplus.be.server.domain.order.command.UseCouponCommand;
import kr.hhplus.be.server.domain.order.facade.OrderFacade;
import kr.hhplus.be.server.domain.product.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

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
        List<OrderProductCommand> productCommands = List.of(
                new OrderProductCommand(1L, 1)
        );
        OrderPaymentCommand paymentCommand = new OrderPaymentCommand(1000, 0, 1000);
        UseBalanceCommand useBalanceCommand = new UseBalanceCommand(userId, 1000);
        List<UseCouponCommand> useCouponCommands = List.of();
        
        OrderCommand orderCommand = new OrderCommand(
                productCommands,
                paymentCommand,
                useBalanceCommand,
                useCouponCommands
        );

        doNothing().when(orderService).createOrder(userId, orderCommand);
        doNothing().when(productService).decreaseStock(productCommands);
        doNothing().when(balanceService).useBalance(useBalanceCommand);
        doNothing().when(issuedCouponService).useCoupon(useCouponCommands);
        doNothing().when(dataPlatformService).sendOrderData();

        // When
        orderFacade.createOrder(userId, orderCommand);

        // Then
        verify(orderService).createOrder(userId, orderCommand);
        verify(productService).decreaseStock(productCommands);
        verify(balanceService).useBalance(useBalanceCommand);
        verify(issuedCouponService).useCoupon(useCouponCommands);
        verify(dataPlatformService).sendOrderData();
    }

    @Test
    @DisplayName("[성공] 쿠폰을 사용한 주문 생성")
    void 쿠폰_사용_주문_생성_성공() {
        // Given
        Long userId = 1L;
        Long issuedCouponId = 1L;
        List<OrderProductCommand> productCommands = List.of(
                new OrderProductCommand(1L, 2)
        );
        OrderPaymentCommand paymentCommand = new OrderPaymentCommand(2000, 1000, 1000);
        UseBalanceCommand useBalanceCommand = new UseBalanceCommand(userId, 1000);
        List<UseCouponCommand> useCouponCommands = List.of(
                new UseCouponCommand(userId, issuedCouponId)
        );
        
        OrderCommand orderCommand = new OrderCommand(
                productCommands,
                paymentCommand,
                useBalanceCommand,
                useCouponCommands
        );

        doNothing().when(orderService).createOrder(userId, orderCommand);
        doNothing().when(productService).decreaseStock(productCommands);
        doNothing().when(balanceService).useBalance(useBalanceCommand);
        doNothing().when(issuedCouponService).useCoupon(useCouponCommands);
        doNothing().when(dataPlatformService).sendOrderData();

        // When
        orderFacade.createOrder(userId, orderCommand);

        // Then
        verify(orderService).createOrder(userId, orderCommand);
        verify(productService).decreaseStock(productCommands);
        verify(balanceService).useBalance(useBalanceCommand);
        verify(issuedCouponService).useCoupon(useCouponCommands);
        verify(dataPlatformService).sendOrderData();
    }

    @Test
    @DisplayName("[실패] 주문 생성 실패 - 재고 부족")
    void 주문_생성_실패_재고_부족() {
        // Given
        Long userId = 1L;
        List<OrderProductCommand> productCommands = List.of(
                new OrderProductCommand(1L, 2)
        );
        OrderPaymentCommand paymentCommand = new OrderPaymentCommand(2000, 0, 2000);
        UseBalanceCommand useBalanceCommand = new UseBalanceCommand(userId, 2000);
        List<UseCouponCommand> useCouponCommands = List.of();
        
        OrderCommand orderCommand = new OrderCommand(
                productCommands,
                paymentCommand,
                useBalanceCommand,
                useCouponCommands
        );

        doNothing().when(orderService).createOrder(userId, orderCommand);
        doThrow(new IllegalArgumentException("재고가 부족합니다."))
                .when(productService).decreaseStock(productCommands);

        // When, Then
        assertThatThrownBy(() -> orderFacade.createOrder(userId, orderCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("재고가 부족합니다.");

        verify(orderService).createOrder(userId, orderCommand);
        verify(productService).decreaseStock(productCommands);
        verify(balanceService, never()).useBalance(any());
        verify(issuedCouponService, never()).useCoupon(any());
        verify(dataPlatformService, never()).sendOrderData();
    }

    @Test
    @DisplayName("[실패] 주문 생성 실패 - 잔액 부족")
    void 주문_생성_실패_잔액_부족() {
        // Given
        Long userId = 1L;
        List<OrderProductCommand> productCommands = List.of(
                new OrderProductCommand(1L, 2)
        );
        OrderPaymentCommand paymentCommand = new OrderPaymentCommand(2000, 0, 2000);
        UseBalanceCommand useBalanceCommand = new UseBalanceCommand(userId, 2000);
        List<UseCouponCommand> useCouponCommands = List.of();
        
        OrderCommand orderCommand = new OrderCommand(
                productCommands,
                paymentCommand,
                useBalanceCommand,
                useCouponCommands
        );

        doNothing().when(orderService).createOrder(userId, orderCommand);
        doNothing().when(productService).decreaseStock(productCommands);
        doThrow(new IllegalArgumentException("잔고를 찾을 수 없습니다."))
                .when(balanceService).useBalance(useBalanceCommand);

        // When, Then
        assertThatThrownBy(() -> orderFacade.createOrder(userId, orderCommand))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("잔고를 찾을 수 없습니다.");

        verify(orderService).createOrder(userId, orderCommand);
        verify(productService).decreaseStock(productCommands);
        verify(balanceService).useBalance(useBalanceCommand);
        verify(issuedCouponService, never()).useCoupon(any());
        verify(dataPlatformService, never()).sendOrderData();
    }
}
