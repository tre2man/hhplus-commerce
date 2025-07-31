package kr.hhplus.be.server.domain.order.vo;

import lombok.Getter;

@Getter
public class CreateOrderProductVo {
    private final Long productId;
    private final Integer quantity;
    private final Integer price;

    public CreateOrderProductVo(Long productId, Integer quantity, Integer price) {
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
    }

    public static CreateOrderProductVo of(Long productId, Integer quantity, Integer price) {
        return new CreateOrderProductVo(productId, quantity, price);
    }
}
