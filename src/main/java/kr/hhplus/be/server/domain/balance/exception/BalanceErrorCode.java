package kr.hhplus.be.server.domain.balance.exception;

import kr.hhplus.be.server.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BalanceErrorCode implements ErrorCode {
    OVER_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 초과되었습니다."),
    LOW_BALANCE(HttpStatus.BAD_REQUEST, "잔액이 부족합니다."),
    CHARGE_OVER_BALANCE(HttpStatus.BAD_REQUEST, "충전 잔액이 초과되었습니다."),
    CHARGE_LOW_BALANCE(HttpStatus.BAD_REQUEST, "충전 잔액이 부족합니다."),
    NOT_FOUND_BALANCE(HttpStatus.NOT_FOUND, "잔고를 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
