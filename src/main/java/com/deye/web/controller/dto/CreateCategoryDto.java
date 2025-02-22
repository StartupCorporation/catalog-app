package com.deye.web.controller.dto;

import com.deye.web.validation.annotation.ImageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateCategoryDto {

    @NotBlank(message = "Name can't be null or blank")
    @Length(min = 3, max = 50, message = "Name length must be between 3 and 50")
    private String name;

    @NotBlank(message = "Description can't be null or blank")
    @Length(min = 10, max = 255, message = "Description length must be between 10 and 250")
    private String description;

    @NotNull(message = "Image can't be null")
    @ImageType
    private MultipartFile image;

    @Valid
    private List<CategoryAttributeDto> attributes;
}
