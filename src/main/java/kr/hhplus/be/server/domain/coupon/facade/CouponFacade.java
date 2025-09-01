package kr.hhplus.be.server.domain.coupon.facade;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.command.CreateIssuedCouponCommand;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.coupon.service.IssuedCouponService;
import kr.hhplus.be.server.domain.coupon.vo.UserCouponVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CouponFacade {
    private final CouponService couponService;
    private final IssuedCouponService issuedCouponService;

    @Transactional
    public void issueCoupon(Long userId, Long couponId) {
        couponService.issueCoupon(couponId);
        Integer expireDay = couponService.getCouponExpireDay(couponId);
        issuedCouponService.createIssuedCoupon(
                new CreateIssuedCouponCommand(userId, couponId, expireDay)
        );
    }

    public List<UserCouponVo> getUserCoupons(Long userId) {
        return issuedCouponService.getUserCoupons(userId);
    }
}
