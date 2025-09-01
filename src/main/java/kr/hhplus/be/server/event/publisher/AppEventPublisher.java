package kr.hhplus.be.server.event.publisher;

import kr.hhplus.be.server.event.event.BaseEvent;

public interface AppEventPublisher {
    void publish(BaseEvent event);
}
