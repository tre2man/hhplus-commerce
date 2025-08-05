package kr.hhplus.be.server.domain.order.command;

public record UseBalanceCommand (
    Long userId,
    Integer useAmount
) {
}
