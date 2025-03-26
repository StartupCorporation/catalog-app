package com.deye.web.util.mapper;

import com.deye.web.controller.dto.CreateImageDto;
import com.deye.web.controller.dto.DeleteImageDto;
import com.deye.web.controller.dto.response.ImageResponseDto;
import com.deye.web.entity.FileEntity;
import com.deye.web.service.FileService;
import com.deye.web.service.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ImageMapper {
    private final FileService fileService;
    private final FileStorageService fileStorageService;

    public List<DeleteImageDto> toDeleteImageDtoList(Collection<FileEntity> files) {
        List<DeleteImageDto> deleteImages = new ArrayList<>();
        if (files != null) {
            for (FileEntity file : files) {
                deleteImages.add(new DeleteImageDto(file.getName(), file.getDirectory()));
            }
        }
        return deleteImages;
    }

    public List<CreateImageDto> toCreateImageDtoList(Collection<FileEntity> filesEntities, MultipartFile[] images) {
        List<CreateImageDto> createImages = new ArrayList<>();
        if (images != null && filesEntities != null) {
            for (MultipartFile image : images) {
                Optional<FileEntity> file = fileService.getFileEntityByFile(filesEntities, image);
                if (file.isPresent()) {
                    createImages.add(new CreateImageDto(image, file.get()));
                }
            }
        }
        return createImages;
    }

    public ImageResponseDto toImageResponseDto(FileEntity fileEntity) {
        ImageResponseDto imageResponseDto = new ImageResponseDto();
        imageResponseDto.setId(fileEntity.getId());
        imageResponseDto.setLink(fileStorageService.getAccessLink(fileEntity.getDirectory(), fileEntity.getName()));
        return imageResponseDto;
    }
}
