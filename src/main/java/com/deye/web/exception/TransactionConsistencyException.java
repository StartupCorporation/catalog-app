package com.deye.web.exception;

public class TransactionConsistencyException extends CommonException {

    public TransactionConsistencyException(Integer code, String message) {
        super(code, message);
    }

    public TransactionConsistencyException(Throwable throwable) {
        super(throwable);
    }
}
