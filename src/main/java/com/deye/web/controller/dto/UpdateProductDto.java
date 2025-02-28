package com.deye.web.controller.dto;

import com.deye.web.validation.annotation.ImageType;
import com.deye.web.validation.annotation.NotEmptyModel;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NotEmptyModel
public class UpdateProductDto {

    @Length(min = 3, max = 50, message = "Name length must be between 3 and 50")
    private String name;

    @Length(min = 10, max = 255, message = "Description length must be between 10 and 250")
    private String description;

    @Positive(message = "Price must be greater then 0")
    private Float price;

    @PositiveOrZero(message = "Quantity on stock can't be less then 0")
    private Integer stockQuantity;

    @ImageType
    private MultipartFile[] imagesToAdd;
    private List<String> imagesToRemove;

    @Valid
    private Map<UUID, Object> attributesValuesToSave;
    private Set<UUID> attributesIdsToRemove;
}
