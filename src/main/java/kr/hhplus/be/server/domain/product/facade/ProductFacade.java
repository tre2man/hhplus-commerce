package kr.hhplus.be.server.domain.product.facade;

import kr.hhplus.be.server.domain.order.service.OrderProductService;
import kr.hhplus.be.server.domain.product.dto.GetProductResponse;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.product.vo.ProductVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;
    private final OrderProductService orderProductService;

    public List<GetProductResponse> getAllProduct() {
        return productService.getAllProducts()
                .stream()
                .map(GetProductResponse::of)
                .toList();

    }

    // TODO: 에러 처리 개선 필요
    public GetProductResponse getProductById(Long productId) {
        Optional<ProductVo> productVo = productService.findProductById(productId);
        if (productVo.isEmpty()) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다. productId: " + productId);
        }
        return GetProductResponse.of(productVo.get());
    }

    /**
     * 3일 내 주문량이 가장 많은 상품 5개를 조회합니다.
     */
    public List<GetProductResponse> getPopularProducts() {
        List<Long> productIdList = orderProductService.getPopular5ProductIds();
        return productService.getProductsByIds(productIdList)
                .stream()
                .map(GetProductResponse::of)
                .toList();

    }
}
