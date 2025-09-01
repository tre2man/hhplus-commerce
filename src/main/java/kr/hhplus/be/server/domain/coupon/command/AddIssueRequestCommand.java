package kr.hhplus.be.server.domain.coupon.command;

public record AddIssueRequestCommand (
    Long userId,
    Long couponId
) {}
