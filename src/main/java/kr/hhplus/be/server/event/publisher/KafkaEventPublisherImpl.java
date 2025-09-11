package kr.hhplus.be.server.event.publisher;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.event.event.BaseEvent;
import kr.hhplus.be.server.event.enums.KafkaTopic;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaEventPublisherImpl implements KafkaEventPublisher {
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public void publish(BaseEvent event, KafkaTopic topic) {
        try {
            String topicName = topic.getTopicName();
            String eventJson = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(topicName, event.getKey(), eventJson);
        } catch (Exception e) {
            // TODO: 에러 정규화
            log.error("이벤트 발행에 실패했습니다. eventId: {}", event.getId());
            throw new RuntimeException("이벤트 발행에 실패했습니다.", e);
        }
    }
}
