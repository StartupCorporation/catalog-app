package com.deye.web.exception.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DatabaseConstraintErrorResponseDto extends ErrorResponseDto {
    private String constraintName;

}
