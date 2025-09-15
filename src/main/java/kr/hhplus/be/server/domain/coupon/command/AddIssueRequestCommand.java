package kr.hhplus.be.server.domain.coupon.command;

import kr.hhplus.be.server.event.event.CouponIssueRequestEvent;

public record AddIssueRequestCommand (
    Long userId,
    Long couponId,
    Integer expireDays
) {
    public CouponIssueRequestEvent toCouponIssueRequestEvent() {
        return new CouponIssueRequestEvent(userId, couponId, expireDays);
    }
}
