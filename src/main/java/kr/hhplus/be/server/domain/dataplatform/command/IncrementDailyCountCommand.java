package kr.hhplus.be.server.domain.dataplatform.command;

public record IncrementDailyCountCommand(
    Long productId,
    Integer count
) {
}
