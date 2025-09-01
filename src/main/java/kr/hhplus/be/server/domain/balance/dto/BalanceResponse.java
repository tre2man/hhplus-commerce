package kr.hhplus.be.server.domain.balance.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "현재 잔액 확인")
public class BalanceResponse {
    @Schema(description = "현재 잔액", example = "1000")
    private Integer balance;

    private BalanceResponse(Integer balance) {
        this.balance = balance;
    }

    public static BalanceResponse of(Integer balance) {
        return new BalanceResponse(balance);
    }
}
