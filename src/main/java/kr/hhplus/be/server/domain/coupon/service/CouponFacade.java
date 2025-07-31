package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.vo.UserCouponVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CouponFacade {
    private final CouponService couponService;
    private final IssuedCouponService issuedCouponService;

    public CouponFacade(CouponService couponService, IssuedCouponService issuedCouponService) {
        this.couponService = couponService;
        this.issuedCouponService = issuedCouponService;
    }

    public void issueCoupon(Long userId, Long couponId) {
        Integer expireDay = couponService.getCouponExpireDay(couponId);
        issuedCouponService.createIssuedCoupon(userId, couponId, expireDay);
        couponService.issueCoupon(couponId);
    }

    public List<UserCouponVo> getUserCoupons(Long userId) {
        return issuedCouponService.getUserCouponByUserId(userId);
    }
}
