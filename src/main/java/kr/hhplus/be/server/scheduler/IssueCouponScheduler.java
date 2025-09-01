package kr.hhplus.be.server.scheduler;

import kr.hhplus.be.server.domain.coupon.facade.CouponFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class IssueCouponScheduler {
    private final CouponFacade couponFacade;

    // 매 5초마다 쿠폰 발급 요청을 처리합니다.
    @Scheduled(fixedRate = 5000)
    public void processCouponIssuance() {
        couponFacade.issueCouponRequest();
    }
}
