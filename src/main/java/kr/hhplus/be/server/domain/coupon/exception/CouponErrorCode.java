package kr.hhplus.be.server.domain.coupon.exception;

import kr.hhplus.be.server.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum CouponErrorCode implements ErrorCode {
    OVER_COUPON_ISSUE_COUNT(HttpStatus.CONFLICT, "쿠폰 발급 가능 수량을 초과했습니다."),
    EXHAUSTED_COUPON(HttpStatus.CONFLICT, "발행 가능한 쿠폰이 없습니다."),
    ALREADY_COUPON_ISSUE_REQUESTED_USER(HttpStatus.CONFLICT, "이미 쿠폰 발행을 요청한 사용자입니다."),
    ALREADY_USED_COUPON(HttpStatus.CONFLICT, "이미 사용된 쿠폰입니다."),
    NOT_FOUND_COUPON(HttpStatus.NOT_FOUND, "쿠폰을 찾을 수 없습니다."),
    NOT_USED_COUPON(HttpStatus.CONFLICT, "사용되지 않은 쿠폰입니다."),
    EXPIRED_COUPON(HttpStatus.CONFLICT, "만료된 쿠폰입니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
