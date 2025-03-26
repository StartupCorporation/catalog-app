package com.deye.web.util.mapper;

import com.deye.web.controller.dto.response.AttributeResponseDto;
import com.deye.web.controller.dto.response.ImageResponseDto;
import com.deye.web.controller.dto.response.ProductResponseDto;
import com.deye.web.entity.ProductEntity;
import com.deye.web.service.FileService;
import com.deye.web.service.file.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
public class ProductMapper {
    private final AttributeMapper attributeMapper;
    private final FileService fileService;
    private final FileStorageService fileStorageService;

    public ProductResponseDto toProductResponseDto(ProductEntity product) {
        List<AttributeResponseDto> attributes = product.getAttributesValuesForProduct().stream()
                .map(attributeMapper::toAttributeView)
                .toList();
        Map<String, List<String>> directoriesWithFileNames = fileService.getDirectoriesWithFilesNames(product.getImages());
        Set<ImageResponseDto> images = new HashSet<>();
        for (String directoryName : directoriesWithFileNames.keySet()) {
            List<String> fileNames = directoriesWithFileNames.get(directoryName);
            for (String fileName : fileNames) {
                ImageResponseDto imageDto = new ImageResponseDto();
                String link = fileStorageService.getAccessLink(directoryName, fileName);
                UUID fileId = product.getImages().stream()
                        .filter(image -> image.getName().equals(fileName))
                        .findAny()
                        .get()
                        .getId();
                imageDto.setLink(link);
                imageDto.setId(fileId);
                images.add(imageDto);
            }
        }
        return ProductResponseDto.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .images(images)
                .attributes(attributes)
                .build();
    }
}
