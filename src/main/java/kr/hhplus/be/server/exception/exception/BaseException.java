package kr.hhplus.be.server.exception.exception;

import kr.hhplus.be.server.exception.code.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
}
