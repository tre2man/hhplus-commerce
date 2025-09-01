package kr.hhplus.be.server.event.publisher;

import kr.hhplus.be.server.event.event.BaseEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ConditionalOnProperty(name = "event.publisher.type", havingValue = "spring", matchIfMissing = true)
public class SpringEventPublisher implements AppEventPublisher {
    private final ApplicationEventPublisher applicationEventPublisher;

    @Override
    public void publish(BaseEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}

