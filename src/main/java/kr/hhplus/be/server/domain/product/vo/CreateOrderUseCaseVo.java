package kr.hhplus.be.server.domain.product.vo;

import lombok.Getter;

import java.util.List;
import java.util.Optional;

@Getter
public class CreateOrderUseCaseVo {
    private final Long userId;
    private final List<CreateOrderProductUseCaseVo> productVoList;
    private final Optional<Long> issuedCouponId;

    private CreateOrderUseCaseVo(Long userId, List<CreateOrderProductUseCaseVo> productList, Optional<Long> issuedCouponId) {
        this.userId = userId;
        this.productVoList = productList;
        this.issuedCouponId = issuedCouponId;
    }

    public static CreateOrderUseCaseVo of(Long userId, List<CreateOrderProductUseCaseVo> productList, Optional<Long> issuedCouponId) {
        return new CreateOrderUseCaseVo(userId, productList, issuedCouponId);
    }
}
