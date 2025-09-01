package kr.hhplus.be.server.domain.order.command;

public record OrderProductCommand(
    Long productId,
    Integer quantity
) {
}
