package kr.hhplus.be.server.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.product.vo.ProductVo;
import lombok.Getter;

@Schema(description = "상품 정보 조회")
@Getter
public class GetProductResponse {
    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품 이름", example = "상품1")
    private String name;

    @Schema(description = "상품 가격", example = "10000")
    private Integer price;

    @Schema(description = "남은 재고 수량", example = "50")
    private Integer stock;

    public GetProductResponse(Long id, String name, Integer price, Integer stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public static GetProductResponse of(ProductVo productVo) {
        return new GetProductResponse(
                productVo.getId(),
                productVo.getName(),
                productVo.getPrice(),
                productVo.getStock()
        );
    }
}
