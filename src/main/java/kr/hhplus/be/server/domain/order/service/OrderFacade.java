package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.dataPlatform.service.DataPlatformService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.entity.OrderProduct;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.product.vo.CreateOrderProductUseCaseVo;
import kr.hhplus.be.server.domain.product.vo.CreateOrderUseCaseVo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;
    private final BalanceService balanceService;
    private final DataPlatformService dataPlatformService;

    public OrderFacade(
            OrderService orderService,
            ProductService productService,
            BalanceService balanceService,
            DataPlatformService dataPlatformService
    ) {
        this.orderService = orderService;
        this.productService = productService;
        this.balanceService = balanceService;
        this.dataPlatformService = dataPlatformService;
    }

    public Order createOrder(CreateOrderUseCaseVo createOrderUseCaseVo) {
        // 재고 확인
        List<CreateOrderProductUseCaseVo> productVoList = createOrderUseCaseVo.getProductList();
        for (CreateOrderProductUseCaseVo product : productVoList) {
            if (!productService.checkStock(product.getProductId(), product.getQuantity())) {
                throw new IllegalArgumentException("재고가 부족합니다: " + product.getProductId());
            }
        }

        // 잔액 차감
        Integer totalPrice = productVoList.stream()
                .mapToInt(product -> productService.getProductById(product.getProductId()).getPrice() * product.getQuantity())
                .sum();
        balanceService.useBalance(createOrderUseCaseVo.getUserId(), totalPrice);

        // 주문 생성
        Order order = Order.create(
                createOrderUseCaseVo.getUserId(),
                totalPrice,
                totalPrice,
                "주문이 성공적으로 생성되었습니다."
        );
        for (CreateOrderProductUseCaseVo productVo : productVoList) {
            Product originalProduct = productService.getProductById(productVo.getProductId());
            order.addOrderProduct(OrderProduct.create(
                    order,
                    productVo.getProductId(),
                    originalProduct.getPrice(),
                    productVo.getQuantity()
            ));
        }
        orderService.save(order);

        // 데이터 플랫폼에 주문 데이터 전송
        this.dataPlatformService.sendOrderData(order);

        return order;
    }
}
