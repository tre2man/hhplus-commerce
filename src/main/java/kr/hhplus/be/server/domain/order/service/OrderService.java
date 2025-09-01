package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderProduct;
import kr.hhplus.be.server.domain.order.repository.OrderRepository;
import kr.hhplus.be.server.domain.order.vo.CreateOrderProductVo;
import kr.hhplus.be.server.domain.order.vo.CreateOrderVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(CreateOrderVo createorderVo) {
        // 주문 생성
        Order order = Order.create(
                createorderVo.getUserId(),
                createorderVo.getTotalAmount(),
                createorderVo.getFinalAmount(),
                "주문이 성공적으로 생성되었습니다."
        );
        List<CreateOrderProductVo> productVoList = createorderVo.getProductList();
        for (CreateOrderProductVo productVo : productVoList) {
            order.addOrderProduct(OrderProduct.create(
                    order,
                    productVo.getProductId(),
                    productVo.getPrice(),
                    productVo.getQuantity()
            ));
        }
        return orderRepository.save(order);
    }
}
