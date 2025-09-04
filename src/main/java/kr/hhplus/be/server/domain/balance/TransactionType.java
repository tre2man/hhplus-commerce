package kr.hhplus.be.server.domain.balance;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionType {
    CHARGE("충전"),
    CHARGE_COMPENSATING("충전 오류"),
    USE("사용"),
    USE_COMPENSATING("사용 오류");

    private final String description;
}
