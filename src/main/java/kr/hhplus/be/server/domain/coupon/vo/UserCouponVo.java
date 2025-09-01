package kr.hhplus.be.server.domain.coupon.vo;

import kr.hhplus.be.server.domain.coupon.dto.UserCouponResponse;
import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserCouponVo {
    private final Long userId;
    private final Long couponId;
    private final Long issuedCouponId;
    private final LocalDateTime expireAt;

    private UserCouponVo(IssuedCoupon issuedCoupon) {
        this.userId = issuedCoupon.getUserId();
        this.couponId = issuedCoupon.getCouponId();
        this.issuedCouponId = issuedCoupon.getId();
        this.expireAt = issuedCoupon.getExpireAt();
    }

    public UserCouponResponse toResponse() {
        return UserCouponResponse.of(this);
    }

    public static UserCouponVo of(IssuedCoupon issuedCoupon) {
        return new UserCouponVo(issuedCoupon);
    }
}
