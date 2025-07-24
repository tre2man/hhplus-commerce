package kr.hhplus.be.server.domain.balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "현재 잔액 확인")
public class BalanceResponse {
    @Schema(description = "현재 잔액", example = "1000")
    private Long balance;

    public BalanceResponse(
            Long balance
    ) {
        this.balance = balance;
    }

    public Long getBalance() {
        return balance;
    }
}
