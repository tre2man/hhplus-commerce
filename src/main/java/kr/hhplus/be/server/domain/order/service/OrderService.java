package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.event.enums.KafkaTopic;
import kr.hhplus.be.server.event.event.OrderRequestedEvent;
import kr.hhplus.be.server.event.publisher.KafkaEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final KafkaEventPublisher eventPublisher;
    private final OrderPaymentService orderPaymentService;
    private final OrderProductService orderProductService;
    private final OrderRepository orderRepository;

    public void createOrderEvent(OrderCommand orderCommand) {
        OrderRequestedEvent event = orderCommand.toOrderRequestedEvent();
        eventPublisher.publish(event, KafkaTopic.ORDER_REQUEST);
    }

    @Transactional
    public Long createOrder(OrderCommand orderCommand) {
        Order order = Order.create(orderCommand.userId(), null);
        orderRepository.save(order);

        Long orderId = order.getId();
        orderPaymentService.create(orderId, orderCommand.paymentCommand());
        orderProductService.create(orderId, orderCommand.productCommandList());
        
        return orderId; // 생성된 orderId 반환
    }

    @Transactional
    public void createOrderCompensation(Long orderId) {
        orderPaymentService.delete(orderId);
        orderProductService.delete(orderId);
        orderRepository.deleteById(orderId);
    }
}
