package kr.hhplus.be.server.domain.product.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "상품 정보 조회")
public class GetProductResponse {
    @Schema(description = "상품 ID", example = "1")
    private Long id;

    @Schema(description = "상품 이름", example = "상품1")
    private String name;

    @Schema(description = "상품 설명", example = "상품1 설명")
    private String description;

    @Schema(description = "상품 가격", example = "10000")
    private Long price;

    @Schema(description = "남은 재고 수량", example = "50")
    private Integer stock;

    public GetProductResponse(Long id, String name, String description, Long price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getPrice() {
        return price;
    }
}
