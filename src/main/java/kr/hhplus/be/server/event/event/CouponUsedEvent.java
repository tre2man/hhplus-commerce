package kr.hhplus.be.server.event.event;

import com.fasterxml.jackson.annotation.JsonIgnore;
import kr.hhplus.be.server.domain.dataplatform.command.SendOrderDataCommand;
import kr.hhplus.be.server.domain.order.command.OrderProductCommand;
import kr.hhplus.be.server.utils.StringUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CouponUsedEvent implements BaseEvent {
    private String id;
    private String key;
    private Long userId;
    private Long orderId;
    private List<OrderProductCommand> productCommandList; // 데이터 플랫폼 전송용

    public CouponUsedEvent(
            Long userId,
            Long orderId,
            List<OrderProductCommand> productCommandList
    ) {
        this.id = StringUtils.getKafkaId();
        this.key = userId.toString();
        this.userId = userId;
        this.orderId = orderId;
        this.productCommandList = productCommandList;
    }

    @JsonIgnore
    public List<SendOrderDataCommand> toSendOrderDataCommandList() {
        return productCommandList.stream()
            .map(productCommand -> new SendOrderDataCommand(
                productCommand.productId(),
                productCommand.quantity()
            ))
            .toList();
    }
}