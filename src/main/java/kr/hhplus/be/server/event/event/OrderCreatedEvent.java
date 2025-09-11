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
public class OrderCreatedEvent implements BaseEvent {
    private String id;
    private String key;
    private Long userId;
    private Long orderId;
    private UseBalanceCommand useBalanceCommand;
    private List<OrderProductCommand> productCommandList;
    private List<UseCouponCommand> useCouponCommandList;

    public OrderCreatedEvent(
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
    public BalanceUsedEvent toBalanceUsedEvent() {
        return new BalanceUsedEvent(userId, orderId, useBalanceCommand, productCommandList, useCouponCommandList);
    }
}