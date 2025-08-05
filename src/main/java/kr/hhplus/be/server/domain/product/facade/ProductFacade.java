package kr.hhplus.be.server.domain.product.facade;

import kr.hhplus.be.server.domain.order.service.OrderProductService;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;
    private final OrderProductService orderProductService;

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
