package com.deye.web.controller.dto;

import com.deye.web.validation.annotation.ImageType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class FileDto {

    @NotNull(message = "File can't be null")
    @ImageType
    private MultipartFile file;

    @NotNull(message = "Order number can't be null")
    @Positive(message = "Order number must be positive")
    private Integer order;
}
