package kr.hhplus.be.server.exception.exception;

import kr.hhplus.be.server.exception.code.ErrorCode;

public class RestApiException extends BaseException {
    public RestApiException(ErrorCode errorCode) {
        super(errorCode);
    }
}