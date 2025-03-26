package com.deye.web.service;

import com.deye.web.entity.CategoryEntity;
import com.deye.web.entity.FileEntity;
import com.deye.web.exception.ActionNotAllowedException;
import com.deye.web.security.dto.IdentityDetailsDto;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileService {

    public FileEntity createFileEntity(MultipartFile file) {
        String name = extractFileNameFromFile(file);
        String directory = generateFileDirectoryName();
        return new FileEntity(name, directory);
    }

    public String extractFileNameFromFile(MultipartFile file) {
        return file.getOriginalFilename();
    }

    private String generateFileDirectoryName() {
        IdentityDetailsDto identityDetailsDto = (IdentityDetailsDto) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return "identity-" + identityDetailsDto.getId() + "-directory";
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
        Set<FileEntity> allFilesRelatedToCategory = category.getProducts().stream()
                .flatMap(product -> product.getImages().stream())
                .collect(Collectors.toSet());
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
