package kr.hhplus.be.server.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.order.command.OrderProductCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderProductRequest {
    @Schema(description = "상품 ID", example = "12345")
    private Long productId;

    @Schema(description = "주문 수량", example = "2")
    private Integer quantity;

    public OrderProductCommand toCommand() {
        return new OrderProductCommand(productId, quantity);
    }
}
