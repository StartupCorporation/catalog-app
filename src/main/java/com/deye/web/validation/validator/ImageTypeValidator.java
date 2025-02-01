package com.deye.web.validation.validator;

import com.deye.web.validation.annotation.ImageType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

public class ImageTypeValidator implements ConstraintValidator<ImageType, Object> {

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true;
        }
        if (value instanceof MultipartFile file) {
            return isValidContentType(file.getContentType());
        }

        if (value instanceof MultipartFile[] files) {
            for (MultipartFile file : files) {
                if (file == null) continue;
                if (!isValidContentType(file.getContentType())) return false;
            }
        }

        return false;
    }

    private boolean isValidContentType(String contentType) {
        return MediaType.IMAGE_JPEG_VALUE.equals(contentType) || MediaType.IMAGE_PNG_VALUE.equals(contentType);
    }
}
