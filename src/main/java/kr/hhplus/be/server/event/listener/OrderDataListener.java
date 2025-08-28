package kr.hhplus.be.server.event.listener;

import kr.hhplus.be.server.domain.dataplatform.service.DataPlatformService;
import kr.hhplus.be.server.event.event.OrderDataEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderDataListener extends AbstractAppEventListener<OrderDataEvent> {
    private final DataPlatformService dataPlatformService;

    @Override
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void listen(OrderDataEvent event) {
        log.info("OrderDataListener listen");
        dataPlatformService.sendOrderData(event.toOrderDataCommandList());
        log.info("OrderDataListener sendOrderData completed");
    }
}
