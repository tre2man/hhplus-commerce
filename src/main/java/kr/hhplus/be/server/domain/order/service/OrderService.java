package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.command.OrderCommand;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderPaymentService orderPaymentService;
    private final OrderProductService orderProductService;
    private final OrderRepository orderRepository;


    public void createOrder(Long userId, OrderCommand orderCommand) {
        Order order = create(userId);
        orderPaymentService.create(order.getId(), orderCommand.paymentCommand());
        orderProductService.create(order.getId(), orderCommand.productCommandList());
    }

    private Order create(Long userId) {
        Order order = Order.create(userId, null);
        orderRepository.save(order);
        return order;
    }
}
