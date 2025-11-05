package kr.hhplus.be.server.event.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.hhplus.be.server.domain.coupon.command.CreateIssuedCouponCommand;
import kr.hhplus.be.server.utils.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class CouponIssueRequestEvent implements BaseEvent {
    private String id;
    private String key;
    private Long userId;
    private Long couponId;
    private Integer expireDays;

    public CouponIssueRequestEvent(Long userId, Long couponId, Integer expireDays) {
        this.id = StringUtils.getKafkaId();
        this.key = couponId.toString();
        this.userId = userId;
        this.couponId = couponId;
        this.expireDays = expireDays;
    }

    @JsonIgnore
    public CreateIssuedCouponCommand toCommand(int expireDays) {
        return new CreateIssuedCouponCommand(userId, couponId, expireDays);
    }
}
