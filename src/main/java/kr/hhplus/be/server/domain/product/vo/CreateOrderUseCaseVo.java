package kr.hhplus.be.server.domain.product.vo;

import lombok.Getter;

import java.util.Optional;

@Getter
public class CreateOrderUseCaseVo {
    private Optional<Long> issuedCouponId;
    private Long userId;
    private CreateOrderProductUseCaseVo product;

    private CreateOrderUseCaseVo() {}

    public CreateOrderUseCaseVo(Long userId, CreateOrderProductUseCaseVo product, Optional<Long> issuedCouponId) {
        this.userId = userId;
        this.product = product;
        this.issuedCouponId = issuedCouponId;
    }

    public static CreateOrderUseCaseVo of(Long userId, CreateOrderProductUseCaseVo product, Optional<Long> issuedCouponId) {
        return new CreateOrderUseCaseVo(userId, product, issuedCouponId);
    }
}
