package kr.hhplus.be.server.event.event;

import kr.hhplus.be.server.domain.dataplatform.command.SendOrderDataCommand;

import java.util.List;

public class OrderDataEvent implements BaseEvent{
    List<SendOrderDataCommand> orderDataCommandList;

    public OrderDataEvent(List<SendOrderDataCommand> orderDataCommandList) {
        this.orderDataCommandList = orderDataCommandList;
    }

    public List<SendOrderDataCommand> toOrderDataCommandList() {
        return orderDataCommandList;
    }

    /**
     * TODO: 구현 필요
     */
    @Override
    public String getEventId() {
        return "";
    }

    /**
     * TODO: 구현 필요
     */
    @Override
    public String getEventType() {
        return OrderDataEvent.class.getSimpleName();
    }
}
