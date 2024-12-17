package com.deye.web.exception;

import lombok.Getter;

@Getter
public abstract class CommonException extends RuntimeException {
    private final Integer code;

    protected CommonException(Integer code, String message) {
        super(message);
        this.code = code;
    }
}
