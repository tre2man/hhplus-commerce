package kr.hhplus.be.server.domain.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class IssueCouponRequest {
    @Schema(description = "사용자 ID", example = "1")
    private Long userId;

    @Schema(description = "발행된 쿠폰 ID", example = "1001")
    private Long couponId;
}
