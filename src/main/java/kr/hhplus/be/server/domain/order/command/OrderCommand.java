package kr.hhplus.be.server.domain.order.command;

import java.util.List;

public record OrderCommand(
    List<OrderProductCommand> productCommandList,
    OrderPaymentCommand paymentCommand,
    UseBalanceCommand useBalanceCommand,
    List<UseCouponCommand> useCouponCommandList
) {}

