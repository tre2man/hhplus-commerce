package kr.hhplus.be.server.event.publisher;

import kr.hhplus.be.server.event.event.BaseEvent;

public abstract class AbstractAppEventPublisher<T extends BaseEvent> implements AppEventPublisher<T> {
    @Override
    public void publish(T event) {}
}
