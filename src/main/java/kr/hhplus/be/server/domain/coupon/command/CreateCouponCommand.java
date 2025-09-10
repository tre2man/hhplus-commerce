package kr.hhplus.be.server.domain.coupon.command;

public record CreateCouponCommand(
        String name,
        Integer discountAmount,
        Integer totalQuantity,
        Integer expireDay
) {
}
