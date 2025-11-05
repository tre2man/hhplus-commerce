package kr.hhplus.be.server.domain.coupon.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.command.CreateCouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.exception.CouponErrorCode;
import kr.hhplus.be.server.domain.coupon.repository.CouponInfoRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssueCountRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssueUserRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.exception.exception.RestApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponIssueCountRepository couponIssueCountRepository;
    private final CouponIssueUserRepository couponIssueUserRepository;
    private final CouponInfoRepository couponInfoRepository;

    @Transactional
    public void issueCoupon(Long couponId, int count) {
        Coupon coupon = couponRepository.findByIdForUpdate(couponId)
                .orElseThrow(() -> new RestApiException(CouponErrorCode.NOT_FOUND_COUPON));
        coupon.issue(count);
        couponRepository.save(coupon);
    }

    @Transactional
    public void createCoupon(CreateCouponCommand command) {
        Coupon coupon = Coupon.create(
                command.name(),
                command.discountAmount(),
                command.totalQuantity(),
                command.expireDay()
        );
        couponRepository.save(coupon);
        couponIssueCountRepository.createCouponIssuableCount(coupon.getId(), command.totalQuantity());
        couponIssueUserRepository.createCouponIssueUsers(coupon.getId());
        couponInfoRepository.createCouponInfo(coupon.getId(), command.expireDay());
    }

    public Integer getCouponExpireDay(Long couponId) {
        return couponInfoRepository.getCouponExpireDay(couponId)
                .orElseThrow(() -> new RestApiException(CouponErrorCode.NOT_FOUND_COUPON));
    }

    public void validateAndAddCouponRequest(Long couponId, Long userId) {
        // 쿠폰 중복 요청 확인
        if (couponIssueUserRepository.isCouponIssueRequested(couponId, userId)) {
            throw new RestApiException(CouponErrorCode.ALREADY_COUPON_ISSUE_REQUESTED_USER);
        }

        // 발행 가능한 쿠폰이 있는지 확인
        Integer issuableCount = couponIssueCountRepository.decrementIssuableCount(couponId, 1);
        if (issuableCount < 0) {
            throw new RestApiException(CouponErrorCode.EXHAUSTED_COUPON);
        }

        // 쿠폰 발행 사용자 등록
        couponIssueUserRepository.addCouponIssueRequestUserId(couponId, userId);
    }
}
