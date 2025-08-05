package kr.hhplus.be.server.domain.order.command;

public record OrderPaymentCommand(
    Integer orderAmount,
    Integer discountAmount,
    Integer usedAmount
) {
}
