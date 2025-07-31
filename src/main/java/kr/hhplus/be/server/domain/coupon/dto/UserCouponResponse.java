package kr.hhplus.be.server.domain.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public class UserCouponResponse {
    @Schema(description = "쿠폰 ID", example = "1")
    private Long couponId;

    @Schema(description = "발급된 쿠폰 ID", example = "12345")
    private Long issuedCouponId;

    @Schema(description = "쿠폰 이름", example = "10% 할인 쿠폰")
    private String name;

    @Schema(description = "총 발급 가능 수량", example = "100")
    private Integer totalQuantity;

    @Schema(description = "발급된 수량", example = "50")
    private Integer issuedQuantity;

    @Schema(description = "쿠폰 만료 시간", example = "2023-12-31T23:59:59")
    private LocalDateTime expireAt;

    private UserCouponResponse(Long couponId, Long issuedCouponId, String name, Integer totalQuantity, Integer issuedQuantity, LocalDateTime expireAt) {
        this.couponId = couponId;
        this.issuedCouponId = issuedCouponId;
        this.name = name;
        this.totalQuantity = totalQuantity;
        this.issuedQuantity = issuedQuantity;
        this.expireAt = expireAt;
    }

    public static UserCouponResponse from(Long couponId, Long issuedCouponId, String name, Integer totalQuantity, Integer issuedQuantity, LocalDateTime expireAt) {
        return new UserCouponResponse(couponId, issuedCouponId, name, totalQuantity, issuedQuantity, expireAt);
    }
}
