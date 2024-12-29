package com.deye.web.exception.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponseDto {
    private Integer code;
    private String message;
}
