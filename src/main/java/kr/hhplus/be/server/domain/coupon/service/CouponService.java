package kr.hhplus.be.server.domain.coupon.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.vo.CouponConstraintVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;

    @Transactional
    public void issueCoupon(Long couponId, int count) {
        Coupon coupon = couponRepository.findByIdForUpdate(couponId)
                .orElseThrow(IllegalArgumentException::new);
        coupon.issue(count);
        couponRepository.save(coupon);
    }

    public CouponConstraintVo getCouponConstraint(Long couponId) {
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(IllegalArgumentException::new);
        return CouponConstraintVo.of(coupon);
    }
}
