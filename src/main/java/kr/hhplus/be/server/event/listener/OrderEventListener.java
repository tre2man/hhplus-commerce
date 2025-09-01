package kr.hhplus.be.server.event.listener;

import kr.hhplus.be.server.domain.dataplatform.service.DataPlatformService;
import kr.hhplus.be.server.event.event.OrderDataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventListener extends AbstractAppEventListener<OrderDataEvent> {
    private final DataPlatformService dataPlatformService;

    @Override
    public void handle(OrderDataEvent event) {
        log.info("OrderEventListener handle: {}", event);
        dataPlatformService.sendOrderData(event.toOrderDataCommandList());
    }
}
