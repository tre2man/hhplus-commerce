package kr.hhplus.be.server.domain.product.dto;


import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인기 정보 조회")
public class GetPopularProductResponse extends GetProductResponse {
    @Schema(description = "판매 횟수", example = "100")
    private Integer salesCount;

    public GetPopularProductResponse(Long id, String name, String description, Long price, Integer salesCount) {
        super(id, name, description, price);
        this.salesCount = salesCount;
    }

    public Integer getSalesCount() {
        return salesCount;
    }
}
