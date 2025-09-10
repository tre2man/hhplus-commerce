package kr.hhplus.be.server.domain.coupon.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.coupon.command.CreateCouponCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateCouponRequest {
    @Schema(description = "쿠폰 이름", example = "할인 쿠폰")
    private String name;

    @Schema(description = "할인 금액", example = "1000")
    private Integer discountAmount;

    @Schema(description = "총 발행 수량", example = "100")
    private Integer totalQuantity;

    @Schema(description = "유효 기간(일)", example = "30")
    private Integer expireDay;

    public CreateCouponCommand toCreateCouponCommand() {
        return new CreateCouponCommand(name, discountAmount, totalQuantity, expireDay);
    }
}
