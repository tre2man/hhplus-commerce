package kr.hhplus.be.server.domain.order.command;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record UseBalanceCommand (
    @JsonProperty("userId") Long userId,
    @JsonProperty("useAmount") Integer useAmount
) {
    @JsonCreator
    public UseBalanceCommand(
        @JsonProperty("userId") Long userId,
        @JsonProperty("useAmount") Integer useAmount
    ) {
        this.userId = userId;
        this.useAmount = useAmount;
    }
}
