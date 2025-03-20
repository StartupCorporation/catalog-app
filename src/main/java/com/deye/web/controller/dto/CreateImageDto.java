package com.deye.web.controller.dto;

import com.deye.web.entity.FileEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateImageDto {
    private MultipartFile image;
    private String fileName;
    private String directoryName;

    public CreateImageDto(MultipartFile image, FileEntity fileEntity) {
        this.image = image;
        this.fileName = fileEntity.getName();
        this.directoryName = fileEntity.getDirectory();
    }
}
