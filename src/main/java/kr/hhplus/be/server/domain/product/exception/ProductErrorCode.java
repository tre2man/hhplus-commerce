package kr.hhplus.be.server.domain.product.exception;

import kr.hhplus.be.server.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode implements ErrorCode {
    NEGATIVE_STOCK(HttpStatus.CONFLICT, "재고는 음수가 될 수 없습니다."),
    INSUFFICIENT_STOCK(HttpStatus.CONFLICT, "재고가 부족합니다."),
    NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
