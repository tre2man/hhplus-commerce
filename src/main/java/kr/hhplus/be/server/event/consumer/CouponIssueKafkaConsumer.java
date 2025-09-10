package kr.hhplus.be.server.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.service.CouponService;
import kr.hhplus.be.server.domain.coupon.service.IssuedCouponService;
import kr.hhplus.be.server.event.enums.KafkaTopic;
import kr.hhplus.be.server.event.event.CouponIssueRequestEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponIssueKafkaConsumer {
    private final IssuedCouponService issuedCouponService;
    private final CouponService couponService;

    private <T> T parseEvent(String eventJson, Class<T> eventType) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(eventJson, eventType);
        } catch (Exception e) {
            log.error("Failed to parse event JSON for type {}", eventType.getSimpleName(), e);
            throw new RuntimeException("Failed to parse event JSON", e);
        }
    }

    @KafkaListener(topics = KafkaTopic.name.COUPON_ISSUE_REQUEST, groupId = KafkaTopic.groupId.ORDER_COMPLETE, batch = "true")
    public void couponIssue(List<String> eventJson, Acknowledgment ack) {
        log.info("쿠폰 발급 이벤트 수신: {}", eventJson.size());
        try {
            List<CouponIssueRequestEvent> eventList = new ArrayList<>();
            for (String eventStr : eventJson) {
                eventList.add(parseEvent(eventStr, CouponIssueRequestEvent.class));
            }
            issuedCouponService.createBulkIssuedCoupon(
                    eventList.stream().
                            map(event -> event.toCommand(event.getExpireDays())).
                            toList()
            );
            couponService.issueCoupon(
                    eventList.get(0).getCouponId(),
                    eventList.size()
            );
            ack.acknowledge();
        } catch(Exception e) {
            log.error("Error processing coupon issue event: {}", e.getMessage());
            /**
             * 배치로 발행한 데이터는 양이 많이 때문에 바로 rollback 을 사용하면 서버에 부하가 걸릴 수 있다.
             * 따라서 이러한 경우에는 슬랙 알림을 통해 개발자가 직접 확인 후 조치할 수 있도록 한다.
             */
        }
    }
}
