package kr.hhplus.be.server.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public class OrderProductRequest {
    @Schema(description = "상품 ID", example = "12345")
    private Long productId;

    @Schema(description = "주문 수량", example = "2")
    private Integer quantity;

    public OrderProductRequest(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public Integer getQuantity() {
        return quantity;
    }
}
