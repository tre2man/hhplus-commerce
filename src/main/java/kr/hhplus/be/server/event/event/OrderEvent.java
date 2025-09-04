package kr.hhplus.be.server.event.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.hhplus.be.server.domain.order.command.*;
import kr.hhplus.be.server.utils.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Optional;

@Getter
@Setter
@NoArgsConstructor
public class OrderEvent implements BaseEvent {
    private String id;
    private String key;
    private Long userId;
    private Optional<Long> orderId;
    private List<OrderProductCommand> productCommandList;
    private OrderPaymentCommand paymentCommand;
    private UseBalanceCommand useBalanceCommand;
    private List<UseCouponCommand> useCouponCommandList;

    public OrderEvent(
            Long userId,
            Optional<Long> orderId,
            List<OrderProductCommand> productCommandList,
            OrderPaymentCommand paymentCommand,
            UseBalanceCommand useBalanceCommand,
            List<UseCouponCommand> useCouponCommandList
    ) {
        this.id = StringUtils.getKafkaId();
        this.key = userId.toString();
        this.userId = userId;
        this.orderId = orderId;
        this.productCommandList = productCommandList;
        this.paymentCommand = paymentCommand;
        this.useBalanceCommand = useBalanceCommand;
        this.useCouponCommandList = useCouponCommandList;
    }

    public OrderCommand toOrderCommand() {
        return new OrderCommand(
                userId,
                orderId,
                productCommandList,
                paymentCommand,
                useBalanceCommand,
                useCouponCommandList
        );
    }

    @JsonIgnore
    public Long getOrderId() {
        if (orderId.isPresent()) {
            return orderId.get();
        } else {
            throw new IllegalStateException("OrderId 가 존재하지 않습니다.");
        }
    }
}
