package kr.hhplus.be.server.domain.order.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.order.command.UseBalanceCommand;
import lombok.Getter;

@Getter
public class OrderUseBalanceRequest {
    @Schema(description = "사용자 ID", example = "67890")
    Long userId;

    @Schema(description = "사용할 잔액 금액", example = "10000")
    Integer useAmount;

    public UseBalanceCommand toCommand() {
        return new UseBalanceCommand(userId, useAmount);
    }
}
