package com.deye.web.exception;

public class EntityNotFoundException extends CommonException {

    public EntityNotFoundException(Integer code, String message) {
        super(code, message);
    }
}
