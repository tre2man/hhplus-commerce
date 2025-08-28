package kr.hhplus.be.server.event.listener;

import kr.hhplus.be.server.event.event.BaseEvent;
import org.springframework.stereotype.Service;

@Service
public interface AppEventListener<T extends BaseEvent> {
    void listen(T event);
}
