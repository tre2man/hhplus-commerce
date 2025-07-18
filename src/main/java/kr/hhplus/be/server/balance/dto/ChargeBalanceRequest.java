package kr.hhplus.be.server.balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Schema(description = "잔액 충전 요청")
public class ChargeBalanceRequest {
    @Schema(description = "충전할 금액", example = "1000")
    @Min(value = 1L, message = "충전 금액은 1원 이상이어야 합니다.")
    @Max(value = 5_000_000, message = "충전 금액은 5,000,000원을 초과할 수 없습니다.")
    private Long amount;

    public Long getAmount() {
        return amount;
    }
}
