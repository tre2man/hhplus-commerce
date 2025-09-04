package kr.hhplus.be.server.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.coupon.service.IssuedCouponService;
import kr.hhplus.be.server.domain.dataplatform.service.DataPlatformService;
import kr.hhplus.be.server.domain.order.service.OrderService;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.event.enums.KafkaTopic;
import kr.hhplus.be.server.event.event.*;
import kr.hhplus.be.server.event.publisher.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventKafkaConsumer {
    private final KafkaEventPublisher eventPublisher;
    private final OrderService orderService;
    private final BalanceService balanceService;
    private final ProductService productService;
    private final IssuedCouponService issuedCouponService;
    private final DataPlatformService dataPlatformService;

    private <T> T parseEvent(String eventJson, Class<T> eventType) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(eventJson, eventType);
        } catch (Exception e) {
            log.error("Failed to parse event JSON for type {}", eventType.getSimpleName(), e);
            throw new RuntimeException("Failed to parse event JSON", e);
        }
    }

    @KafkaListener(topics = KafkaTopic.name.ORDER_REQUEST, groupId = KafkaTopic.groupId.ORDER_COMPLETE)
    public void createOrder(String eventJson) {
        OrderRequestedEvent event = parseEvent(eventJson, OrderRequestedEvent.class);
        Long orderId;

        try {
            orderId = orderService.createOrder(event.toOrderCommand());
        } catch (Exception e) {
            log.error("Business logic failed - triggering compensation, eventId: {}", event.getId(), e);
            return;
        }

        eventPublisher.publish(event.toOrderCreatedEvent(orderId), KafkaTopic.ORDER_COMPLETE);
    }

    @KafkaListener(topics = KafkaTopic.name.ORDER_COMPLETE, groupId = KafkaTopic.groupId.USE_BALANCE)
    public void useBalance(String eventJson) {
        OrderCreatedEvent event = parseEvent(eventJson, OrderCreatedEvent.class);
        
        try {
            balanceService.useBalance(event.getUseBalanceCommand());
        } catch (Exception e) {
            log.error("Business logic failed - triggering compensation, eventId: {}", event.getId(), e);
            eventPublisher.publish(event, KafkaTopic.ORDER_COMPLETE_COMPENSATION);
            return;
        }

        eventPublisher.publish(event.toBalanceUsedEvent(), KafkaTopic.USE_BALANCE_COMPLETE);
    }

    @KafkaListener(topics = KafkaTopic.name.USE_BALANCE_COMPLETE, groupId = KafkaTopic.groupId.DECREASE_STOCK)
    public void decreaseStock(String eventJson) {
        BalanceUsedEvent event = parseEvent(eventJson, BalanceUsedEvent.class);
        
        try {
            productService.decreaseStock(event.getProductCommandList());
        } catch (Exception e) {
            log.error("Business logic failed - triggering compensation, eventId: {}", event.getId(), e);
            eventPublisher.publish(event, KafkaTopic.USE_BALANCE_COMPENSATION);
            return;
        }

        eventPublisher.publish(event.toStockDecreasedEvent(), KafkaTopic.DECREASE_STOCK_COMPLETE);
    }

    @KafkaListener(topics = KafkaTopic.name.DECREASE_STOCK_COMPLETE, groupId = KafkaTopic.groupId.USE_COUPON)
    public void useCoupon(String eventJson) {
        StockDecreasedEvent event = parseEvent(eventJson, StockDecreasedEvent.class);
        
        try {
            issuedCouponService.useCoupon(event.getUseCouponCommandList());
        } catch (Exception e) {
            log.error("Business logic failed - triggering compensation, eventId: {}", event.getId(), e);
            eventPublisher.publish(event, KafkaTopic.DECREASE_STOCK_COMPENSATION);
            return;
        }

        eventPublisher.publish(event.toCouponUsedEvent(), KafkaTopic.USE_COUPON_COMPLETE);
    }

    @KafkaListener(topics = KafkaTopic.name.USE_COUPON_COMPLETE, groupId = KafkaTopic.groupId.SEND_DATA_PLATFORM)
    public void addDataPlatform(String eventJson) {
        CouponUsedEvent event = parseEvent(eventJson, CouponUsedEvent.class);
        
        try {
            dataPlatformService.sendOrderData(event.toSendOrderDataCommandList());
        } catch (Exception e) {
            log.error("Business logic failed - triggering compensation, eventId: {}", event.getId(), e);
            // 데이터 플랫폼 전송에 실패했을 경우에는 롤백 트랜잭션을 실행하지 않는다.
        }
    }
}
