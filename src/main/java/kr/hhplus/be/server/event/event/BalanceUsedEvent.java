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
public class BalanceUsedEvent implements BaseEvent {
    private String id;
    private String key;
    private Long userId;
    private Long orderId;
    private UseBalanceCommand useBalanceCommand; // 보상 시 필요
    private List<OrderProductCommand> productCommandList; // 재고 감소용
    private List<UseCouponCommand> useCouponCommandList; // 이후 쿠폰 사용용

    public BalanceUsedEvent(
            Long userId,
            Long orderId,
            UseBalanceCommand useBalanceCommand,
            List<OrderProductCommand> productCommandList,
            List<UseCouponCommand> useCouponCommandList
    ) {
        this.id = StringUtils.getKafkaId();
        this.key = userId.toString();
        this.userId = userId;
        this.orderId = orderId;
        this.useBalanceCommand = useBalanceCommand;
        this.productCommandList = productCommandList;
        this.useCouponCommandList = useCouponCommandList;
    }

    @JsonIgnore
    public StockDecreasedEvent toStockDecreasedEvent() {
        return new StockDecreasedEvent(userId, orderId, useBalanceCommand, useCouponCommandList, productCommandList);
    }
    
    @JsonIgnore
    public OrderCreatedEvent toOrderCreatedEventForCompensation() {
        // 보상 시 BalanceUsedEvent → OrderCreatedEvent 변환
        return new OrderCreatedEvent(
            userId,
            orderId,
            useBalanceCommand, // 보상 시 필요한 useBalanceCommand 유지
            productCommandList,
            useCouponCommandList
        );
    }
}