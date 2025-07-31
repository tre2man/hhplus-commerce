package kr.hhplus.be.server.domain.order.vo;

import lombok.Getter;

@Getter
public class CreateOrderCouponVo {
    private final Long couponId;
    private final Long issuedCouponId;
    private final Integer discountAmount;

    private CreateOrderCouponVo(Long couponId, Long issuedCouponId, Integer discountAmount) {
        this.couponId = couponId;
        this.issuedCouponId = issuedCouponId;
        this.discountAmount = discountAmount;
    }

    public static CreateOrderCouponVo of(Long couponId, Long issuedCouponId, Integer discountAmount) {
        return new CreateOrderCouponVo(couponId, issuedCouponId, discountAmount);
    }
}
