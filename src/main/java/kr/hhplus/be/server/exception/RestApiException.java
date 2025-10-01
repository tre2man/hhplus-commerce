package kr.hhplus.be.server.exception;

public class RestApiException extends BaseException {
    public RestApiException(ErrorCode errorCode) {
        super(errorCode);
    }
}