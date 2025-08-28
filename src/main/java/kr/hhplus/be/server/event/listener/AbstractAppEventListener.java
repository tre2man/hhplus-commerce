package kr.hhplus.be.server.event.listener;

import kr.hhplus.be.server.event.event.BaseEvent;
import org.springframework.context.event.EventListener;

public abstract class AbstractAppEventListener<T extends BaseEvent> implements AppEventListener<T> {
    @EventListener
    public void listen(T event) {}
}
