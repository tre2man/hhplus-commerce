package kr.hhplus.be.server.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.order.command.OrderPaymentCommand;
import lombok.Getter;

@Getter
public class OrderPaymentRequest {
    @Schema(description = "주문 금액", example = "10000")
    private Integer orderAmount;

    @Schema(description = "할인 금액", example = "2000")
    private Integer discountAmount;

    @Schema(description = "사용된 금액", example = "5000")
    private Integer usedAmount;

    public OrderPaymentCommand toCommand() {
        return new OrderPaymentCommand(orderAmount, discountAmount, usedAmount);
    }
}
