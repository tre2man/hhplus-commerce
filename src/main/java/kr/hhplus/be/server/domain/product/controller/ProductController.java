package kr.hhplus.be.server.domain.product.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.domain.product.dto.GetProductResponse;
import kr.hhplus.be.server.domain.product.facade.ProductFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/product")
@Tag(name = "Product", description = "상품 API")
public class ProductController {
    private final ProductFacade productFacade;

    @Operation(summary = "상품 조회", description = "모든 상품의 정보를 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "상품 정보 조회 성공")
    @GetMapping
    public ResponseEntity<List<GetProductResponse>> getAllProducts() {
        List<GetProductResponse> responseList = productFacade.getAllProduct();
        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "특정 상품 조회", description = "특정 상품의 정보를 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "상품 정보 조회 성공")
    @ApiResponse(responseCode = "404", description = "상품을 찾을 수 없음")
    @GetMapping("/{productId}")
    public ResponseEntity<GetProductResponse> getProduct(@PathVariable long productId) {
        GetProductResponse response = productFacade.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "인기 상품 조회", description = "인기 상품의 정보를 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "인기 상품 정보 조회 성공")
    @GetMapping("/popular")
    public ResponseEntity<List<GetProductResponse>> getPopularProducts() {
        Long start = System.currentTimeMillis();
        List<GetProductResponse> responseList = productFacade.getPopularProducts();
        Long end = System.currentTimeMillis();
        System.out.println("상품 조회 시간: " + (end - start) + "ms");
        return ResponseEntity.ok(responseList);
    }
}
