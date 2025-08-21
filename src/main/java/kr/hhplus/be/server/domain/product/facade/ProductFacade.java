package kr.hhplus.be.server.domain.product.facade;

import kr.hhplus.be.server.config.cache.CacheNames;
import kr.hhplus.be.server.domain.dataplatform.entity.OrderRankProduct;
import kr.hhplus.be.server.domain.dataplatform.service.DataPlatformService;
import kr.hhplus.be.server.domain.product.dto.GetPopularProductResponse;
import kr.hhplus.be.server.domain.product.dto.GetProductResponse;
import kr.hhplus.be.server.domain.product.service.ProductService;
import kr.hhplus.be.server.domain.product.vo.ProductVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductFacade {
    private final ProductService productService;
    private final DataPlatformService dataPlatformService;

    public List<GetProductResponse> getAllProduct() {
        return productService.getAllProducts()
                .stream()
                .map(GetProductResponse::of)
                .toList();

    }

    // TODO: 에러 처리 개선 필요
    @Cacheable(value = CacheNames.PRODUCT_INFO, key = "#productId")
    public GetProductResponse getProductById(Long productId) {
        Optional<ProductVo> productVo = productService.findProductById(productId);
        if (productVo.isEmpty()) {
            throw new IllegalArgumentException("상품을 찾을 수 없습니다. productId: " + productId);
        }
        return GetProductResponse.of(productVo.get());
    }

    /**
     * 3일 내 주문량이 가장 많은 상품 n개를 조회합니다.
     */
    public List<GetPopularProductResponse> getPopularProducts() {
        List<OrderRankProduct> orderRankProductList = dataPlatformService.getTopNOrderProducts(5);
        return orderRankProductList.stream()
                .map(orderRankProduct ->
                    GetPopularProductResponse.of(
                            orderRankProduct.productId(),
                            orderRankProduct.name(),
                            orderRankProduct.price(),
                            orderRankProduct.score()
                    )
                )
                .toList();
    }
}
