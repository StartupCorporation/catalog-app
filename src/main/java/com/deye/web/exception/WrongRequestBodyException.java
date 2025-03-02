package com.deye.web.exception;

public class WrongRequestBodyException extends CommonException {
    public WrongRequestBodyException(Integer code, String message) {
        super(code, message);
    }
}
