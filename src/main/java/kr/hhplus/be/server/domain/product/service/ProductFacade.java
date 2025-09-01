package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.order.service.OrderProductService;
import kr.hhplus.be.server.domain.product.entity.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductFacade {
    private final ProductService productService;
    private final OrderProductService orderProductService;

    public ProductFacade(ProductService productService, OrderProductService orderProductService) {
        this.productService = productService;
        this.orderProductService = orderProductService;
    }

    public List<Product> getAllProducts() {
        return productService.getAllProducts();
    }

    /**
     * 3일 내 주문량이 가장 많은 상품 5개를 조회합니다.
     */
    public List<Product> getPopularProducts() {
        List<Long> productIdList = orderProductService.getPopular5ProductIds();
        return productService.getProductsByIds(productIdList);
    }
}
