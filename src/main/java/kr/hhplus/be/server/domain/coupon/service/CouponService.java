package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import org.springframework.stereotype.Service;

@Service
public class CouponService {
    private final CouponRepository couponRepository;

    public CouponService(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    public void issueCoupon(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(IllegalArgumentException::new);
        coupon.issue();
        couponRepository.save(coupon);
    }

    public Integer getCouponExpireDay(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(IllegalArgumentException::new);
        return coupon.getExpireDay();
    }
}
