package kr.hhplus.be.server.domain.coupon.facade;

import kr.hhplus.be.server.domain.coupon.command.AddIssueRequestCommand;
import kr.hhplus.be.server.domain.coupon.command.CreateIssuedCouponCommand;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.coupon.service.IssueRequestService;
import kr.hhplus.be.server.domain.coupon.service.IssuedCouponService;
import kr.hhplus.be.server.domain.coupon.vo.CouponConstraintVo;
import kr.hhplus.be.server.domain.coupon.vo.UserCouponVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class CouponFacade {
    private final CouponService couponService;
    private final IssuedCouponService issuedCouponService;
    private final IssueRequestService issueRequestService;

    public void addIssueRequest(Long userId, Long couponId) {
        issueRequestService.addIssueRequest(
                new AddIssueRequestCommand(userId, couponId)
        );
    }

    public void issueCouponRequest() {
        // 발행 요청이 들어온 쿠폰 ID 목록을 조회
        List<Long> getIssueRequestCouponIdList = issueRequestService.getIssueRequestCouponIdList();
        List<CreateIssuedCouponCommand> createIssuedCouponCommandList = new ArrayList<>();
        Map<Long, Integer> couponIdToCountMap = new HashMap<>();

        // 발행할 쿠폰 정보 확인
        for (Long couponId : getIssueRequestCouponIdList) {
            List<Long> userIdList = issueRequestService.getIssueRequest(couponId);
            CouponConstraintVo couponConstraint = couponService.getCouponConstraint(couponId);
            List<Long> userIdListToIssue = userIdList.stream()
                    .limit(couponConstraint.issuableCount())
                    .toList();
            for(Long userId : userIdListToIssue) {
                CreateIssuedCouponCommand command = new CreateIssuedCouponCommand(
                        userId,
                        couponId,
                        couponConstraint.getExpireDate()
                );
                createIssuedCouponCommandList.add(command);
            }
            couponIdToCountMap.put(couponId, userIdListToIssue.size());
        }

        // 배치로 대용량 데이터 저장 실행
        issuedCouponService.createBulkIssuedCoupon(createIssuedCouponCommandList);

        // 발행된 쿠폰 차감
        for (Map.Entry<Long, Integer> entry : couponIdToCountMap.entrySet()) {
            Long couponId = entry.getKey();
            Integer count = entry.getValue();
            couponService.issueCoupon(couponId, count);
        }
    }

    public List<UserCouponVo> getUserCoupons(Long userId) {
        return issuedCouponService.getUserCoupons(userId);
    }
}
