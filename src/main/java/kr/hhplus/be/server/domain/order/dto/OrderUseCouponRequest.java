package kr.hhplus.be.server.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.order.command.UseCouponCommand;
import lombok.Getter;

@Getter
public class OrderUseCouponRequest {
    @Schema(description = "사용자 ID", example = "67890")
    Long userId;

    @Schema(description = "사용할 쿠폰 ID", example = "12345")
    Long issuedCouponId;

    public UseCouponCommand toCommand() {
        return new UseCouponCommand(userId, issuedCouponId);
    }
}
