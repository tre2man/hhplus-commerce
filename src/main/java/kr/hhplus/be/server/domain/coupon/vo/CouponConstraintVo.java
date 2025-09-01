package kr.hhplus.be.server.domain.coupon.vo;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import lombok.Getter;

@Getter
public class CouponConstraintVo {
    private final Long couponId;
    private final Integer totalCount;
    private final Integer issuedCount;
    private final Integer expireDate;

    private CouponConstraintVo(Long couponId, Integer totalCount, Integer issuedCount, Integer expireDate) {
        this.couponId = couponId;
        this.totalCount = totalCount;
        this.issuedCount = issuedCount;
        this.expireDate = expireDate;
    }

    public static CouponConstraintVo of(Coupon coupon) {
        return new CouponConstraintVo(
                coupon.getId(),
                coupon.getTotalQuantity(),
                coupon.getIssuedQuantity(),
                coupon.getExpireDay()
        );
    }

    public Integer issuableCount() {
        return totalCount - issuedCount;
    }
}
