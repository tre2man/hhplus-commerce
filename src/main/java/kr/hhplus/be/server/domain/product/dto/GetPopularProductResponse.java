package kr.hhplus.be.server.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.product.vo.ProductVo;
import lombok.Getter;

@Schema(description = "인기 상품 정보 조회")
@Getter
public class GetPopularProductResponse extends GetProductResponse {
    @Schema(description = "판매량", example = "100")
    private Integer orderCount;

    private GetPopularProductResponse(
            Long id,
            String name,
            Integer price,
            Integer stock,
            Integer orderCount
    ) {
        super(id, name, price, stock);
        this.orderCount = orderCount;
    }

     public static GetPopularProductResponse of(
            ProductVo productVo,
            Integer orderCount
    ) {
         return new GetPopularProductResponse(
                productVo.getId(),
                productVo.getName(),
                productVo.getPrice(),
                productVo.getStock(),
                orderCount
         );
     }
}
