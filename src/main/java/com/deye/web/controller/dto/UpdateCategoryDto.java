package com.deye.web.controller.dto;

import com.deye.web.validation.annotation.ImageType;
import com.deye.web.validation.annotation.NotEmptyModel;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NotEmptyModel
public class UpdateCategoryDto {

    @Length(min = 3, max = 50, message = "Name length must be between 3 and 50")
    private String name;

    @Length(min = 10, max = 255, message = "Description length must be between 10 and 250")
    private String description;

    @ImageType
    private MultipartFile image;

    @Valid
    private List<CategoryAttributeDto> attributesToSave;
    private List<UUID> attributesIdsToRemove;
}
