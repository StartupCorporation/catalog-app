package com.deye.web.controller.dto;

import com.deye.web.validation.annotation.ImageType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateProductDto {

    @NotBlank(message = "Name can't be null or blank")
    @Length(min = 3, max = 50, message = "Name length must be between 3 and 50")
    private String name;

    @NotBlank(message = "Description can't be null or blank")
    @Length(min = 10, max = 255, message = "Description length must be between 10 and 250")
    private String description;

    @NotNull(message = "Price can't be null")
    @Positive(message = "Price must be greater then 0")
    private Float price;

    @NotNull(message = "Please provide product quantity on stock")
    @PositiveOrZero(message = "Quantity on stock can't be less then 0")
    private Integer stockQuantity;

    @NotNull(message = "Please, provide category")
    private UUID categoryId;

    @NotNull(message = "Images can't be null")
    @ImageType
    private MultipartFile[] images;

    @Valid
    private Map<UUID, Object> attributesValuesToSave;
}
