package kr.hhplus.be.server.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.order.command.OrderCommand;
import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
@Schema(description = "주문 요청 정보")
public class OrderRequest {
    @Schema(description = "사용자 ID", example = "67890")
    private Long userId;

    @Schema(description = "주문할 상품 목록")
    private List<OrderProductRequest> products;

    @Schema(description = "결제 정보")
    private OrderPaymentRequest payment;

    @Schema(description = "사용할 잔액 정보")
    private OrderUseBalanceRequest useBalance;

    @Schema(description = "사용할 쿠폰 정보", nullable = true)
    private List<OrderUseCouponRequest> useCoupons;

    public OrderCommand toCommand() {
        return new OrderCommand(
                userId,
                Optional.empty(),
                products.stream().map(OrderProductRequest::toCommand).toList(),
                payment.toCommand(),
                useBalance.toCommand(),
                useCoupons != null ? useCoupons.stream().map(OrderUseCouponRequest::toCommand).toList() : List.of()
        );
    }
}
