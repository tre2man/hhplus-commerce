package kr.hhplus.be.server.domain.order.facade;

import kr.hhplus.be.server.aop.lock.MultiDistributedLock;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.coupon.service.IssuedCouponService;
import kr.hhplus.be.server.domain.dataPlatform.service.DataPlatformService;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;
    private final BalanceService balanceService;
    private final DataPlatformService dataPlatformService;
    private final IssuedCouponService issuedCouponService;

    @MultiDistributedLock(
        keyPrefix = "ORDER",
        keyExpression = "#orderCommand.getProductIds()"
    )
    public void createOrder(Long userId, OrderCommand orderCommand) {
        orderService.createOrder(userId, orderCommand);
        productService.decreaseStock(orderCommand.productCommandList());
        balanceService.useBalance(orderCommand.useBalanceCommand());
        issuedCouponService.useCoupon(orderCommand.useCouponCommandList());
        dataPlatformService.sendOrderData();
    }
}
