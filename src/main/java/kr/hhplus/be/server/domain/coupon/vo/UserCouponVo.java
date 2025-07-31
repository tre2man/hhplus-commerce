package kr.hhplus.be.server.domain.coupon.vo;

import kr.hhplus.be.server.domain.coupon.dto.UserCouponResponse;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import lombok.Getter;

@Getter
public class UserCouponVo {
    private Long userId;
    private Long couponId;
    private Long IssuedCouponId;
    private String name;
    private Integer discountAmount;
    private Integer totalQuantity;
    private Integer issuedQuantity;
    private Integer expireDay;

    private UserCouponVo(IssuedCoupon issuedCoupon) {
        this.userId = issuedCoupon.getUserId();
        this.couponId = issuedCoupon.getCoupon().getId();
        this.IssuedCouponId = issuedCoupon.getId();
        this.name = issuedCoupon.getCoupon().getName();
        this.discountAmount = issuedCoupon.getCoupon().getDiscountAmount();
        this.totalQuantity = issuedCoupon.getCoupon().getTotalQuantity();
        this.issuedQuantity = issuedCoupon.getCoupon().getIssuedQuantity();
        this.expireDay = issuedCoupon.getCoupon().getExpireDay();
    }

    public UserCouponResponse toResponse() {
        return UserCouponResponse.builder()
                .couponId(couponId)
                .issuedCouponId(IssuedCouponId)
                .name(name)
                .totalQuantity(totalQuantity)
                .issuedQuantity(issuedQuantity)
                .build();
    }

    public static UserCouponVo of(IssuedCoupon issuedCoupon) {
        return new UserCouponVo(issuedCoupon);
    }
}
