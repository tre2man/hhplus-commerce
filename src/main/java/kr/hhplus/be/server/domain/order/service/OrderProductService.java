package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.order.entity.OrderProduct;
import kr.hhplus.be.server.domain.order.repository.OrderProductRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderProductService {
    private final OrderProductRepository orderProductRepository;

    public OrderProductService(OrderProductRepository orderProductRepository) {
        this.orderProductRepository = orderProductRepository;
    }

    // 3일 내 주문량 상위 5개 상품의 아이디를 반환합니다.
    public List<Long> getPopular5ProductIds() {
        List<OrderProduct> orderProductList = this.orderProductRepository.findAllByCreatedAtAfter(LocalDateTime.now().minusDays(3));
        return orderProductList.stream()
                .collect(Collectors.groupingBy(
                        OrderProduct::getProductId,
                        Collectors.summingInt(OrderProduct::getQuantity)
                ))
                .entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(5)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
