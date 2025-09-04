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
public class OrderRequestedEvent implements BaseEvent {
    private String id;
    private String key;
    private Long userId;
    private List<OrderProductCommand> productCommandList;
    private OrderPaymentCommand paymentCommand;
    private UseBalanceCommand useBalanceCommand;
    private List<UseCouponCommand> useCouponCommandList;

    public OrderRequestedEvent(
            Long userId,
            List<OrderProductCommand> productCommandList,
            OrderPaymentCommand paymentCommand,
            UseBalanceCommand useBalanceCommand,
            List<UseCouponCommand> useCouponCommandList
    ) {
        this.id = StringUtils.getKafkaId();
        this.key = userId.toString();
        this.userId = userId;
        this.productCommandList = productCommandList;
        this.paymentCommand = paymentCommand;
        this.useBalanceCommand = useBalanceCommand;
        this.useCouponCommandList = useCouponCommandList;
    }

    @JsonIgnore
    public OrderCommand toOrderCommand() {
        return new OrderCommand(
                userId,
                null,
                productCommandList,
                paymentCommand,
                useBalanceCommand,
                useCouponCommandList
        );
    }
    
    @JsonIgnore
    public OrderCreatedEvent toOrderCreatedEvent(Long orderId) {
        return new OrderCreatedEvent(
                userId,
                orderId,
                useBalanceCommand,
                productCommandList,
                useCouponCommandList
        );
    }
}