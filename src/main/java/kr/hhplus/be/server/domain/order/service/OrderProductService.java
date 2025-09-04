package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.command.OrderProductCommand;
import kr.hhplus.be.server.domain.order.entity.OrderProduct;
import kr.hhplus.be.server.domain.order.repository.OrderProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;

    public void create(Long orderId, List<OrderProductCommand> command) {
        List<OrderProduct> orderProductList = command.stream()
                .map(cmd -> OrderProduct.create(orderId, cmd.productId(), cmd.quantity()))
                .toList();
        this.orderProductRepository.saveAll(orderProductList);
    }

    public void delete(Long orderId) {
        orderProductRepository.deleteByOrderId(orderId);
    }
}
