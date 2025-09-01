package kr.hhplus.be.server.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class GetProductRequest {
    @Schema(description = "상품 ID", example = "1")
    private Long productId;
}
