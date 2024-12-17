package com.deye.web.exception;

public class MinioException extends CommonException {

    public MinioException(Integer code, String message) {
        super(code, message);
    }
}
