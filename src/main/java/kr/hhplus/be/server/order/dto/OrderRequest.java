package kr.hhplus.be.server.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "주문 요청 정보")
public class OrderRequest {
    @Schema(description = "사용자 ID", example = "67890")
    private Long userId;

    @Schema(description = "주문할 상품 목록")
    private List<OrderProductRequest> products;
    public OrderRequest(Long userId, List<OrderProductRequest> products) {
        this.userId = userId;
        this.products = products;
    }

    public Long getUserId() {
        return userId;
    }

    public List<OrderProductRequest> getProducts() {
        return products;
    }
}
