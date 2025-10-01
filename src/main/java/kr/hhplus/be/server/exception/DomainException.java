package kr.hhplus.be.server.exception;

public class DomainException extends BaseException {
    public DomainException(ErrorCode errorCode) {
        super(errorCode);
    }
}
