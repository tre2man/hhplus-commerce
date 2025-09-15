package kr.hhplus.be.server.domain.coupon.service;

import jakarta.transaction.Transactional;
import kr.hhplus.be.server.domain.coupon.command.CreateCouponCommand;
import kr.hhplus.be.server.domain.coupon.entity.Coupon;
import kr.hhplus.be.server.domain.coupon.repository.CouponInfoRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssueCountRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponIssueUserRepository;
import kr.hhplus.be.server.domain.coupon.repository.CouponRepository;
import kr.hhplus.be.server.domain.coupon.vo.CouponConstraintVo;
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
                .orElseThrow(IllegalArgumentException::new);
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠폰입니다."));
    }

    public void validateAndAddCouponRequest(Long couponId, Long userId) {
        // 쿠폰 중복 요청 확인
        if (couponIssueUserRepository.isCouponIssueRequested(couponId, userId)) {
            throw new IllegalStateException("이미 쿠폰 발행 요청을 한 사용자입니다.");
        }

        // 발행 가능한 쿠폰이 있는지 확인
        Optional<Integer> issuableCount = couponIssueCountRepository.decrementIssuableCount(couponId, 1);
        if (issuableCount.isEmpty()) {
            throw new IllegalArgumentException("존재하지 않는 쿠폰입니다.");
        }
        if (issuableCount.get() < 0) {
            couponIssueCountRepository.setIssuableCount(couponId, 0);
            throw new IllegalStateException("발행 가능한 쿠폰이 없습니다.");
        }

        // 쿠폰 발행 사용자 등록
        couponIssueUserRepository.addCouponIssueRequestUserId(couponId, userId);
    }
}
