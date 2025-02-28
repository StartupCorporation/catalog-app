package com.deye.web.exception;

public class ActionNotAllowedException extends CommonException {
    public ActionNotAllowedException(Integer code, String message) {
        super(code, message);
    }
}
