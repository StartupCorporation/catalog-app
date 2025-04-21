package com.deye.web.service;

import com.deye.web.controller.dto.FileDto;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.entity.FileEntity;
import com.deye.web.entity.ProductEntity;
import com.deye.web.exception.ActionNotAllowedException;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    @Value("${minio.bucket.name}")
    private String bucketName;

    public FileEntity createFileEntity(FileDto fileDto, ProductEntity product) {
        String name = extractFileNameFromFile(fileDto.getFile());
        String directory = generateFileDirectoryName();
        Integer order = fileDto.getOrder();
        validateFileOrder(order, product);
        return new FileEntity(name, directory, order, product);
    }

    private void validateFileOrder(Integer expectedOrder, ProductEntity product) {
        if (expectedOrder == null) {
            throw new ActionNotAllowedException("Please select a valid file order");
        }
        if (product == null) {
            throw new ActionNotAllowedException("Please select a valid product to create file");
        }
        Set<FileEntity> images = product.getImages().stream()
                .sorted(Comparator.comparing(FileEntity::getOrder))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        if (images.isEmpty()) {
            if (expectedOrder != 1) {
                throw new ActionNotAllowedException("You provided wrong image order. Product does not have any images, so correct order is 1");
            }
        }
        // get first skipped order to compare with expected order
        int orderCounter = 1;
        for (FileEntity image : images) {
            if (image.getOrder() != orderCounter) {
                break;
            }
            orderCounter++;
        }
        if (expectedOrder != orderCounter) {
            throw new ActionNotAllowedException("You entered wrong order. Correct order is " + orderCounter);
        }
    }

    public FileEntity createFileEntity(MultipartFile file) {
        String name = extractFileNameFromFile(file);
        String directory = generateFileDirectoryName();
        Integer order = 1;
        return new FileEntity(name, directory, order);
    }

    public String extractFileNameFromFile(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName == null || fileName.isEmpty()) {
            throw new ActionNotAllowedException("File name is empty");
        }
        return fileName;
    }

    private String generateFileDirectoryName() {
        return bucketName;
    }

    public Map<String, List<String>> getDirectoriesWithFilesNames(Collection<FileEntity> filesEntities) {
        return filesEntities.stream()
                .collect(Collectors.groupingBy(FileEntity::getDirectory, Collectors.mapping(FileEntity::getName, Collectors.toList())));
    }

    public Optional<FileEntity> getFileEntityByFile(Collection<FileEntity> filesEntities, MultipartFile file) {
        for (FileEntity fileEntity : filesEntities) {
            if (isFileCorrespondsToFileEntity(file, fileEntity)) {
                return Optional.of(fileEntity);
            }
        }
        return Optional.empty();
    }

    public Set<FileEntity> getAllFilesByCategory(CategoryEntity category) {
        if (category == null) {
            throw new ActionNotAllowedException("Cannot get all files of category because category is null");
        }
        Set<FileEntity> allFilesRelatedToCategory = new HashSet<>();
        if (category.getProducts() != null) {
            allFilesRelatedToCategory = category.getProducts().stream()
                    .flatMap(product -> product.getImages().stream())
                    .collect(Collectors.toSet());
        }
        allFilesRelatedToCategory.add(category.getImage());
        return allFilesRelatedToCategory;
    }

    public boolean isFileCorrespondsToFileEntity(MultipartFile file, FileEntity fileEntity) {
        String fileName = extractFileNameFromFile(file);
        return fileEntity.getName().equals(fileName);
    }

    /**
     * @param filesEntities
     * @param ids
     * @return removed files
     */
    public Set<FileEntity> removeFileEntitiesByIds(Collection<FileEntity> filesEntities, Collection<UUID> ids) {
        if (filesEntities.size() > ids.size()) {
            Set<FileEntity> imagesToRemove = filesEntities.stream()
                    .filter(image -> ids.contains(image.getId()))
                    .collect(Collectors.toSet());
            boolean isRemoved = filesEntities.removeAll(imagesToRemove);
            return isRemoved ? imagesToRemove : Set.of();
        }
        throw new ActionNotAllowedException(ErrorCodeUtils.ACTION_NOT_ALLOWED_ERROR_CODE, ErrorMessageUtils.PRODUCT_IMAGES_DELETION_NOT_ALLOWED_ERROR_MESSAGE);
    }
}
