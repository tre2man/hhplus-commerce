package kr.hhplus.be.server.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.product.dto.GetPopularProductResponse;
import kr.hhplus.be.server.domain.product.dto.GetProductResponse;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.service.ProductFacade;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@Tag(name = "Product", description = "상품 API")
public class ProductController {
    private final ProductFacade productFacade;

    public ProductController(ProductFacade productFacade) {
        this.productFacade = productFacade;
    }

    @Operation(summary = "상품 조회", description = "모든 상품의 정보를 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "상품 정보 조회 성공")
    @GetMapping()
    public ResponseEntity<List<GetProductResponse>> getProducts() {
        List<GetProductResponse> responseList = productFacade.getAllProducts().stream()
                .map(product -> GetProductResponse.of(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice()
                ))
                .toList();
        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "인기 상품 조회", description = "인기 상품의 정보를 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "인기 상품 정보 조회 성공")
    @GetMapping("/popular")
    public ResponseEntity<List<GetProductResponse>> getPopularProducts() {
        List<GetProductResponse> responseList = productFacade.getPopularProducts().stream()
                .map(product -> GetProductResponse.of(
                        product.getId(),
                        product.getName(),
                        product.getDescription(),
                        product.getPrice()
                ))
                .toList();
        return ResponseEntity.ok(responseList);
    }
}
