package kr.hhplus.be.server.domain.product.vo;

import lombok.Getter;

@Getter
public class CreateOrderProductUseCaseVo {
    private final Long productId;
    private final Integer quantity;

    private CreateOrderProductUseCaseVo(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public static CreateOrderProductUseCaseVo of(Long productId, Integer quantity) {
        return new CreateOrderProductUseCaseVo(productId, quantity);
    }
}
