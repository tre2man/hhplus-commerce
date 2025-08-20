package kr.hhplus.be.server.domain.dataplatform.command;

public record CreateOrderDataCommand (
    Long productId,
    Integer orderCount
) {
}
