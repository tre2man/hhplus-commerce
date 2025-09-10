package kr.hhplus.be.server.domain.coupon.facade;

import kr.hhplus.be.server.domain.coupon.command.AddIssueRequestCommand;
import kr.hhplus.be.server.domain.coupon.command.CreateCouponCommand;
import kr.hhplus.be.server.domain.coupon.command.CreateIssuedCouponCommand;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.coupon.service.IssueRequestService;
import kr.hhplus.be.server.domain.coupon.service.IssuedCouponService;
import kr.hhplus.be.server.domain.coupon.vo.CouponConstraintVo;
import kr.hhplus.be.server.domain.coupon.vo.UserCouponVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class CouponFacade {
    private final CouponService couponService;
    private final IssuedCouponService issuedCouponService;
    private final IssueRequestService issueRequestService;

    public void addIssueRequest(Long userId, Long couponId) {
        couponService.validateAndAddCouponRequest(couponId, userId);
        Integer expireDay = couponService.getCouponExpireDay(couponId);
        issueRequestService.addIssueRequest(
                new AddIssueRequestCommand(userId, couponId, expireDay)
        );
    }

    public void createCoupon(CreateCouponCommand command) {
        couponService.createCoupon(command);
    }

    public List<UserCouponVo> getUserCoupons(Long userId) {
        return issuedCouponService.getUserCoupons(userId);
    }
}
