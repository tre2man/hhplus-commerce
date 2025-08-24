package kr.hhplus.be.server.domain.dataplatform.command;

public record sendOrderDataCommand(
    Long productId,
    Integer orderCount
) {
    public IncrementDailyCountCommand toIncrementDailyCountCommand() {
        return new IncrementDailyCountCommand(productId, orderCount);
    }
}
