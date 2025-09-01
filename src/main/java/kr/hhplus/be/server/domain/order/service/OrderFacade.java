package kr.hhplus.be.server.domain.order.service;

import kr.hhplus.be.server.domain.balance.service.BalanceService;
import kr.hhplus.be.server.domain.coupon.service.IssuedCouponService;
import kr.hhplus.be.server.domain.coupon.vo.UserCouponVo;
import kr.hhplus.be.server.domain.dataPlatform.service.DataPlatformService;
import kr.hhplus.be.server.domain.order.entity.Order;
import kr.hhplus.be.server.domain.order.vo.CreateOrderVo;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.product.vo.CreateOrderProductUseCaseVo;
import kr.hhplus.be.server.domain.product.vo.CreateOrderUseCaseVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;
    private final BalanceService balanceService;
    private final DataPlatformService dataPlatformService;
    private final IssuedCouponService issuedCouponService;

    public OrderFacade(
            OrderService orderService,
            ProductService productService,
            BalanceService balanceService,
            DataPlatformService dataPlatformService,
            IssuedCouponService issuedCouponService
    ) {
        this.orderService = orderService;
        this.productService = productService;
        this.balanceService = balanceService;
        this.dataPlatformService = dataPlatformService;
        this.issuedCouponService = issuedCouponService;
    }

    public Order createOrder(CreateOrderUseCaseVo createOrderUseCaseVo) {
        List<CreateOrderProductUseCaseVo> productVoList = createOrderUseCaseVo.getProductVoList();
        for (CreateOrderProductUseCaseVo product : productVoList) {
            if (!productService.checkStock(product.getProductId(), product.getQuantity())) {
                throw new IllegalArgumentException("재고가 부족합니다: " + product.getProductId());
            }
        }

        List<Product> productList = productService.getProductsByIds(
                productVoList.stream()
                        .map(CreateOrderProductUseCaseVo::getProductId)
                        .toList()
        );
        Optional<UserCouponVo> userCoupon = createOrderUseCaseVo.getIssuedCouponId().isPresent()
                ? Optional.ofNullable(issuedCouponService.getUserCouponByIssuedCouponId(createOrderUseCaseVo.getIssuedCouponId().get()))
                : Optional.empty();
        CreateOrderVo createOrderVo = CreateOrderVo.of(
                createOrderUseCaseVo.getUserId(),
                productList,
                productVoList,
                userCoupon,
                createOrderUseCaseVo.getIssuedCouponId()
        );
        balanceService.useBalance(createOrderUseCaseVo.getUserId(), createOrderVo.getFinalAmount());
        Order order = orderService.createOrder(createOrderVo);

        if (createOrderUseCaseVo.getIssuedCouponId().isPresent()) {
            issuedCouponService.useCoupon(createOrderUseCaseVo.getIssuedCouponId().get());
        }

        this.dataPlatformService.sendOrderData();

        return order;
    }
}
