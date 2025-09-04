package kr.hhplus.be.server.event.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.coupon.service.IssuedCouponService;
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
public class OrderEventCompensationKafkaConsumer {
    private final KafkaEventPublisher eventPublisher;
    private final OrderService orderService;
    private final BalanceService balanceService;
    private final ProductService productService;
    private final IssuedCouponService issuedCouponService;

    private <T> T parseEvent(String eventJson, Class<T> eventType) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readValue(eventJson, eventType);
        } catch (Exception e) {
            log.error("Failed to parse event JSON for type {}", eventType.getSimpleName(), e);
            throw new RuntimeException("Failed to parse event JSON", e);
        }
    }

    @KafkaListener(topics = KafkaTopic.name.ORDER_COMPLETE_COMPENSATION, groupId = KafkaTopic.groupId.ORDER_COMPLETE)
    public void createOrder(String eventJson) {
        OrderCreatedEvent event = parseEvent(eventJson, OrderCreatedEvent.class);
        try {
            orderService.createOrderCompensation(event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to handle Kafka event, error: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = KafkaTopic.name.USE_BALANCE_COMPENSATION, groupId = KafkaTopic.groupId.USE_BALANCE)
    public void useBalance(String eventJson) {
        OrderCreatedEvent event = parseEvent(eventJson, OrderCreatedEvent.class);
        log.info("Run Use Balance Compensation, eventId: {}", event.getId());
        try {
            balanceService.useBalanceCompensation(event.getUseBalanceCommand());
            eventPublisher.publish(event, KafkaTopic.ORDER_COMPLETE_COMPENSATION);
        } catch (Exception e) {
            log.error("Failed to handle Kafka event, error: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = KafkaTopic.name.DECREASE_STOCK_COMPENSATION, groupId = KafkaTopic.groupId.DECREASE_STOCK)
    public void decreaseStock(String eventJson) {
        BalanceUsedEvent event = parseEvent(eventJson, BalanceUsedEvent.class);
        log.info("Run Decrease Stock Compensation, eventId: {}", event.getId());
        try {
            productService.increaseStock(event.getProductCommandList());
            eventPublisher.publish(event.toOrderCreatedEventForCompensation(), KafkaTopic.USE_BALANCE_COMPENSATION);
        } catch (Exception e) {
            log.error("Failed to handle Kafka event, error: {}", e.getMessage());
        }
    }

    @KafkaListener(topics = KafkaTopic.name.USE_COUPON_COMPENSATION, groupId = KafkaTopic.groupId.USE_COUPON)
    public void useCoupon(String eventJson) {
        StockDecreasedEvent event = parseEvent(eventJson, StockDecreasedEvent.class);
        log.info("Run Use Coupon Compensation, eventId: {}", event.getId());
        try {
            issuedCouponService.useCouponCompensation(event.getUseCouponCommandList());
            eventPublisher.publish(event.toBalanceUsedEventForCompensation(), KafkaTopic.DECREASE_STOCK_COMPENSATION);
        } catch (Exception e) {
            log.error("Failed to handle Kafka event, error: {}", e.getMessage());
        }
    }

}
