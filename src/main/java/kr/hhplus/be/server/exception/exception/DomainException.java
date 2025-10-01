package kr.hhplus.be.server.exception.exception;

import kr.hhplus.be.server.exception.code.ErrorCode;

public class DomainException extends BaseException {
    public DomainException(ErrorCode errorCode) {
        super(errorCode);
    }
}
