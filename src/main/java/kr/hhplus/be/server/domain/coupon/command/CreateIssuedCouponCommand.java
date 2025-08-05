package kr.hhplus.be.server.domain.coupon.command;

public record CreateIssuedCouponCommand (
    Long userId,
    Long couponId,
    Integer expireDays
) {}
