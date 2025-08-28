package kr.hhplus.be.server.event.publisher;

import kr.hhplus.be.server.event.event.BaseEvent;
import org.springframework.stereotype.Service;

@Service
public interface AppEventPublisher<T extends BaseEvent> {
    void publish(T event);
}
