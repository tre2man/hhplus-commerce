package kr.hhplus.be.server.event.publisher;

import kr.hhplus.be.server.event.event.BaseEvent;
import kr.hhplus.be.server.event.enums.KafkaTopic;

public interface KafkaEventPublisher {
    void publish(BaseEvent event, KafkaTopic topic);
}
