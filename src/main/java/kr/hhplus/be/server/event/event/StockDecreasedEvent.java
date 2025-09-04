package kr.hhplus.be.server.event.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.hhplus.be.server.domain.order.command.*;
import kr.hhplus.be.server.utils.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class StockDecreasedEvent implements BaseEvent {
    private String id;
    private String key;
    private Long userId;
    private Long orderId;
    private UseBalanceCommand useBalanceCommand; // 보상 시 필요
    private List<UseCouponCommand> useCouponCommandList; // 쿠폰 사용용
    private List<OrderProductCommand> productCommandList; // 데이터 플랫폼 전송용

    public StockDecreasedEvent(
            Long userId,
            Long orderId,
            UseBalanceCommand useBalanceCommand,
            List<UseCouponCommand> useCouponCommandList,
            List<OrderProductCommand> productCommandList
    ) {
        this.id = StringUtils.getKafkaId();
        this.key = userId.toString();
        this.userId = userId;
        this.orderId = orderId;
        this.useBalanceCommand = useBalanceCommand;
        this.useCouponCommandList = useCouponCommandList;
        this.productCommandList = productCommandList;
    }

    @JsonIgnore
    public CouponUsedEvent toCouponUsedEvent() {
        return new CouponUsedEvent(userId, orderId, productCommandList);
    }
    
    @JsonIgnore
    public BalanceUsedEvent toBalanceUsedEventForCompensation() {
        // 보상 시 StockDecreasedEvent → BalanceUsedEvent 변환
        return new BalanceUsedEvent(
            userId,
            orderId,
            useBalanceCommand,
            productCommandList,
            useCouponCommandList
        );
    }
}