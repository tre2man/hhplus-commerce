package kr.hhplus.be.server.event.listener;

import kr.hhplus.be.server.event.event.BaseEvent;

public abstract class AbstractAppEventListener<T extends BaseEvent> implements AppEventListener<T> {
    public void handle(T event) {}
}
