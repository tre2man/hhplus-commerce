package kr.hhplus.be.server.product;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.product.dto.GetPopularProductResponse;
import kr.hhplus.be.server.product.dto.GetProductResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/product")
@Tag(name = "Product", description = "상품 API")
public class ProductController {
    @Operation(summary = "상품 조회", description = "특정 상품의 정보를 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "상품 정보 조회 성공")
    @GetMapping()
    public ResponseEntity<List<GetProductResponse>> getProducts() {
        // TODO: 상품 조회 로직 구현
        return ResponseEntity.ok(List.of(
                new GetProductResponse(1L, "상품1", "상품1 설명", 10000L),
                new GetProductResponse(2L, "상품2", "상품2 설명", 20000L)
        ));
    }

    @Operation(summary = "인기 상품 조회", description = "인기 상품의 정보를 조회할 수 있습니다.")
    @ApiResponse(responseCode = "200", description = "인기 상품 정보 조회 성공")
    @GetMapping("/popular")
    public ResponseEntity<List<GetPopularProductResponse>> getPopularProducts() {
        // TODO: 상품 조회 로직 구현
        return ResponseEntity.ok(List.of(
                new GetPopularProductResponse(1L, "상품1", "상품1 설명", 10000L, 100),
                new GetPopularProductResponse(2L, "상품2", "상품2 설명", 20000L, 90),
                new GetPopularProductResponse(3L, "상품3", "상품3 설명", 10000L, 80),
                new GetPopularProductResponse(4L, "상품4", "상품4 설명", 10000L, 70),
                new GetPopularProductResponse(5L, "상품5", "상품5 설명", 10000L, 60)
        ));
    }
}
