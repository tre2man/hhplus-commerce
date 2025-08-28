package kr.hhplus.be.server.event.publisher;

import kr.hhplus.be.server.event.event.OrderDataEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDataPublisher extends AbstractAppEventPublisher<OrderDataEvent> {
    private final ApplicationEventPublisher appEventPublisher;

    @Override
    public void publish(OrderDataEvent event) {
        appEventPublisher.publishEvent(event);
    }
}
