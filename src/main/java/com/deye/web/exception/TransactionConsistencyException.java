package com.deye.web.exception;

public class TransactionConsistencyException extends RuntimeException {

    public TransactionConsistencyException(Exception e) {
        super(e);
    }
}
