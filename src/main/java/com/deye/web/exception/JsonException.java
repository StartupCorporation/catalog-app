package com.deye.web.exception;

public class JsonException extends CommonException {
    public JsonException(Integer code, String message) {
        super(code, message);
    }
}
