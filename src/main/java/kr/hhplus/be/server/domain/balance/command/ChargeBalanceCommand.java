package kr.hhplus.be.server.domain.balance.command;

public record ChargeBalanceCommand(
        Long userId,
        Integer chargeAmount
) {
}
