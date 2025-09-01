package kr.hhplus.be.server.domain.order.command;

public record UseCouponCommand(
    Long userId,
    Long issuedCouponId
) {
}
