package kr.hhplus.be.server.event.listener;

import kr.hhplus.be.server.event.event.BaseEvent;
import org.springframework.context.event.EventListener;

public interface AppEventListener<T extends BaseEvent> {
    @EventListener
    void handle(T event);
}
