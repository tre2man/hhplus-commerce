package kr.hhplus.be.server.domain.order.command;

import kr.hhplus.be.server.event.event.*;

import java.util.List;
import java.util.Optional;

public record OrderCommand(
    Long userId,
    Optional<Long> orderId,
    List<OrderProductCommand> productCommandList,
    OrderPaymentCommand paymentCommand,
    UseBalanceCommand useBalanceCommand,
    List<UseCouponCommand> useCouponCommandList
) {
    public OrderRequestedEvent toOrderRequestedEvent() {
        return new OrderRequestedEvent(userId, productCommandList, paymentCommand, useBalanceCommand, useCouponCommandList);
    }
}

