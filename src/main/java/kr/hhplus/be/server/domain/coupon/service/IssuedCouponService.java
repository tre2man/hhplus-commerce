package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.entity.IssuedCoupon;
import kr.hhplus.be.server.domain.coupon.repository.IssuedCouponRepository;
import kr.hhplus.be.server.domain.coupon.vo.UserCouponVo;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class IssuedCouponService {
    private final IssuedCouponRepository issuedCouponRepository;

    public IssuedCouponService(IssuedCouponRepository issuedCouponRepository) {
        this.issuedCouponRepository = issuedCouponRepository;
    }

    public void createIssuedCoupon(Long userId, Long couponId, Integer expireDays) {
        IssuedCoupon issuedCoupon = IssuedCoupon.create(
                userId,
                couponId,
                LocalDateTime.now().plusDays(expireDays)
        );
        issuedCouponRepository.save(issuedCoupon);
    }

    public void useCoupon(Long issuedCouponId) {
        IssuedCoupon issuedCoupon = issuedCouponRepository.findById(issuedCouponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
        issuedCoupon.use();
        issuedCouponRepository.save(issuedCoupon);
    }

    public UserCouponVo getUserCouponByIssuedCouponId(Long issuedCouponId) {
        IssuedCoupon issuedCoupon = issuedCouponRepository.findById(issuedCouponId)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));
        return UserCouponVo.of(issuedCoupon);
    }

    public List<UserCouponVo> getUserCouponByUserId(Long userId) {
        List<IssuedCoupon> issuedCoupons =  issuedCouponRepository.findByUserId(userId);
        return issuedCoupons.stream()
                .map(UserCouponVo::of)
                .toList();
    }
}
