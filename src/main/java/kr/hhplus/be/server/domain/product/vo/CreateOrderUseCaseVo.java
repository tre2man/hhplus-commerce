package kr.hhplus.be.server.domain.product.vo;

import lombok.Getter;

import java.util.List;

@Getter
public class CreateOrderUseCaseVo {
    private final Long userId;
    private final List<CreateOrderProductUseCaseVo> productList;

    private CreateOrderUseCaseVo(Long userId, List<CreateOrderProductUseCaseVo> productList) {
        this.userId = userId;
        this.productList = productList;
    }

    public static CreateOrderUseCaseVo of(Long userId, List<CreateOrderProductUseCaseVo> productList) {
        return new CreateOrderUseCaseVo(userId, productList);
    }
}
