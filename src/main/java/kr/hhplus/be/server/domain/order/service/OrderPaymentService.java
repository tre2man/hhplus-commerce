package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.command.OrderPaymentCommand;
import kr.hhplus.be.server.domain.order.entity.OrderPayment;
import kr.hhplus.be.server.domain.order.repository.OrderPaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderPaymentService {
    private final OrderPaymentRepository orderPaymentRepository;

    public void create(Long orderId, OrderPaymentCommand command) {
        OrderPayment orderPayment = OrderPayment.create(
                orderId,
                command.orderAmount(),
                command.discountAmount(),
                command.usedAmount()
        );
        orderPaymentRepository.save(orderPayment);
    }
}
