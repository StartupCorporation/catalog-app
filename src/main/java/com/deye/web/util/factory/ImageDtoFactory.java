package com.deye.web.util.factory;

import com.deye.web.controller.dto.CreateImageDto;
import com.deye.web.controller.dto.DeleteImageDto;
import com.deye.web.entity.FileEntity;
import com.deye.web.entity.ProductEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public class ImageDtoFactory {

    public static CreateImageDto createImageDto(MultipartFile file, FileEntity fileEntity) {
        return new CreateImageDto(file, fileEntity);
    }

    public static DeleteImageDto deleteImageDto(String fileName, String directory) {
        return new DeleteImageDto(fileName, directory);
    }

    public static List<DeleteImageDto> deleteImageDtoList(ProductEntity product) {
        List<DeleteImageDto> deleteImages = new ArrayList<>();
        if (product != null) {
            Map<String, List<String>> directoriesWithFileNames = product.getDirectoriesWithFilesNames();
            for (String directory : directoriesWithFileNames.keySet()) {
                List<String> fileNames = directoriesWithFileNames.get(directory);
                for (String fileName : fileNames) {
                    deleteImages.add(deleteImageDto(fileName, directory));
                }
            }
        }
        return deleteImages;
    }

    public static List<DeleteImageDto> deleteImageDtoList(Collection<FileEntity> files) {
        List<DeleteImageDto> deleteImages = new ArrayList<>();
        if (files != null) {
            for (FileEntity file : files) {
                deleteImages.add(deleteImageDto(file.getName(), file.getDirectory()));
            }
        }
        return deleteImages;
    }

    public static List<CreateImageDto> createImageDtos(ProductEntity product, MultipartFile[] images) {
        List<CreateImageDto> createImages = new ArrayList<>();
        if (images != null && product != null) {
            for (MultipartFile image : images) {
                Optional<FileEntity> file = product.getImageByFile(image);
                if (file.isPresent()) {
                    createImages.add(createImageDto(image, file.get()));
                }
            }
        }
        return createImages;
    }
}
