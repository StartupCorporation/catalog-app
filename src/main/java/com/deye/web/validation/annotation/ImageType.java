package com.deye.web.validation.annotation;

import com.deye.web.validation.validator.ImageTypeValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ImageTypeValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ImageType {
    String message() default "Wrong image type";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
