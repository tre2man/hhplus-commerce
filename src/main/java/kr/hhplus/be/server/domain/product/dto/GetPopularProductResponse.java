package kr.hhplus.be.server.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "인기 상품 정보 조회")
@Getter
public class GetPopularProductResponse {
    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품 이름", example = "상품1")
    private String name;

    @Schema(description = "상품 가격", example = "10000")
    private Integer price;

    @Schema(description = "판매량", example = "100")
    private Integer orderCount;

    private GetPopularProductResponse(
            Long id,
            String name,
            Integer price,
            Integer orderCount
    ) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.orderCount = orderCount;
    }

     public static GetPopularProductResponse of(
            Long id,
            String name,
            Integer price,
            Integer orderCount
    ) {
         return new GetPopularProductResponse(
                id,
                name,
                price,
                orderCount
         );
     }
}
