package kr.hhplus.be.server.domain.coupon.service;

import kr.hhplus.be.server.domain.coupon.command.AddIssueRequestCommand;
import kr.hhplus.be.server.event.enums.KafkaTopic;
import kr.hhplus.be.server.event.publisher.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class IssueRequestService {
    private final KafkaEventPublisher eventPublisher;

    public void addIssueRequest(AddIssueRequestCommand command) {
        eventPublisher.publish(command.toCouponIssueRequestEvent(), KafkaTopic.COUPON_ISSUE_REQUEST);
    }
}
