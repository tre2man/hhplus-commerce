package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.coupon.service.IssuedCouponService;
import kr.hhplus.be.server.domain.dataPlatform.service.DataPlatformService;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;
    private final BalanceService balanceService;
    private final DataPlatformService dataPlatformService;
    private final IssuedCouponService issuedCouponService;

    public void createOrder(Long userId, OrderCommand orderCommand) {
        orderService.createOrder(userId, orderCommand);
        productService.decreaseStock(orderCommand.productCommandList());
        balanceService.useBalance(userId, orderCommand.useBalanceCommand().useAmount());
        issuedCouponService.useCoupon(orderCommand.useCouponCommandList());
        dataPlatformService.sendOrderData();
    }
}
