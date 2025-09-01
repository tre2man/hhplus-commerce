package kr.hhplus.be.server.domain.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.vo.UserCouponVo;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserCouponResponse {
    @Schema(description = "쿠폰 ID", example = "1")
    private Long couponId;

    @Schema(description = "발급된 쿠폰 ID", example = "12345")
    private Long issuedCouponId;

    @Schema(description = "쿠폰 만료 시간", example = "2023-12-31T23:59:59")
    private LocalDateTime expireAt;

    private UserCouponResponse(Long couponId, Long issuedCouponId, LocalDateTime expireAt) {
        this.couponId = couponId;
        this.issuedCouponId = issuedCouponId;
        this.expireAt = expireAt;
    }

    public static UserCouponResponse of(UserCouponVo userCouponVo) {
        return new UserCouponResponse(
                userCouponVo.getCouponId(),
                userCouponVo.getIssuedCouponId(),
                userCouponVo.getExpireAt()
        );
    }
}
