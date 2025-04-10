package com.deye.web.service;

import com.deye.web.async.listener.transactions.events.DeletedCategoryEvent;
import com.deye.web.async.listener.transactions.events.SavedCategoryEvent;
import com.deye.web.controller.dto.CreateCategoryDto;
import com.deye.web.controller.dto.CreateImageDto;
import com.deye.web.controller.dto.UpdateCategoryDto;
import com.deye.web.controller.dto.UpdateImageDto;
import com.deye.web.controller.dto.response.CategoryResponseDto;
import com.deye.web.entity.*;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.repository.CategoryRepository;
import com.deye.web.util.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.deye.web.util.error.ErrorCodeUtils.CATEGORY_NOT_FOUND_ERROR_CODE;
import static com.deye.web.util.error.ErrorMessageUtils.CATEGORY_NOT_FOUND_ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CategoryAttributeService categoryAttributeService;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final FileService fileService;
    private final ProductAttributeService productAttributeService;

    /**
     * Method for creating category to add new product type
     *
     * @param categoryDto - category parameters
     */
    @Transactional
    public void create(CreateCategoryDto categoryDto) {
        MultipartFile image = categoryDto.getImage();
        log.info("Creating category: name - {}, description - {} and image - {}", categoryDto.getName(), categoryDto.getDescription(), image.getOriginalFilename());
        CategoryEntity category = createCategory(categoryDto);
        applicationEventPublisher.publishEvent(new SavedCategoryEvent(category, new CreateImageDto(image, category.getImage())));
    }

    private CategoryEntity createCategory(CreateCategoryDto categoryDto) {
        CategoryEntity category = new CategoryEntity();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        FileEntity file = fileService.createFileEntity(categoryDto.getImage());
        category.setImage(file);
        categoryAttributeService.addAttributesToCategory(category, categoryDto.getAttributes());
        return categoryRepository.saveAndFlush(category);
    }

    @Transactional
    public Set<CategoryResponseDto> getAll() {
        List<CategoryEntity> categories = categoryRepository.findAllWithFetchedAttributesAndImage();
        return categories.stream()
                .map(categoryMapper::toCategoryView)
                .collect(Collectors.toSet());
    }

    @Transactional
    public CategoryResponseDto getById(UUID id) {
        log.info("Getting category by id: {}", id);
        CategoryEntity category = getCategoryEntityByIdWithFetchedAttributesInformationAndImage(id);
        log.info("Category found: {}", id);
        return categoryMapper.toCategoryView(category);
    }

    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting category by id: {}", id);
        CategoryEntity category = getCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts(id);
        log.info("Category with id: {} found", id);
        Set<UUID> removedProductsIds = category.getProducts().stream()
                .map(ProductEntity::getId)
                .collect(Collectors.toSet());
        Set<FileEntity> allFilesRelatedToCategory = fileService.getAllFilesByCategory(category);
        categoryRepository.delete(category);
        applicationEventPublisher.publishEvent(new DeletedCategoryEvent(id, fileService.getDirectoriesWithFilesNames(allFilesRelatedToCategory), removedProductsIds));
    }

    @Transactional
    public void update(UUID id, UpdateCategoryDto categoryDto) {
        log.info("Updating category with id: {}", id);
        CategoryEntity category = getCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts(id);
        FileEntity fileEntity = category.getImage();
        if (categoryDto.getName() != null && !categoryDto.getName().equals(category.getName())) {
            category.setName(categoryDto.getName());
            log.info("Category new name is set");
        }
        if (categoryDto.getDescription() != null && !categoryDto.getDescription().equals(category.getDescription())) {
            category.setDescription(categoryDto.getDescription());
            log.info("Category new description is set");
        }
        MultipartFile newImage = categoryDto.getImage();
        String previousImageName = "";
        UpdateImageDto updateImageDto = null;
        if (newImage != null) {
            previousImageName = fileEntity.getName();
            String newImageName = fileService.extractFileNameFromFile(newImage);
            fileEntity.setName(newImageName);
            updateImageDto = new UpdateImageDto(newImage, previousImageName, fileEntity.getDirectory());
            log.info("Category new image is set");
        }
        List<UUID> attributesIdsToRemove = categoryDto.getAttributesIdsToRemove();
        if (attributesIdsToRemove != null && !attributesIdsToRemove.isEmpty()) {
            List<CategoryAttributeEntity> categoryAttributesToRemove = category.getCategoryAttributes().stream()
                    .filter(categoryAttribute -> attributesIdsToRemove.contains(categoryAttribute.getAttribute().getId()))
                    .toList();
            Set<AttributeEntity> attributesToRemoveFromProducts = categoryAttributesToRemove.stream()
                    .map(CategoryAttributeEntity::getAttribute)
                    .collect(Collectors.toSet());
            categoryAttributesToRemove.forEach(category.getCategoryAttributes()::remove);
            for (AttributeEntity attributeEntity : attributesToRemoveFromProducts) {
                if (category.getProducts() != null) {
                    category.getProducts()
                            .forEach(product -> productAttributeService.removeAttributeValue(product, attributeEntity));
                }
            }
        }
        categoryRepository.saveAndFlush(category);
        applicationEventPublisher.publishEvent(new SavedCategoryEvent(category, updateImageDto));
    }

    private CategoryEntity getCategoryEntityByIdWithFetchedAttributesInformationAndImage(UUID id) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findByIdWithFetchedAttributesAndImage(id);
        if (categoryOptional.isEmpty()) {
            log.error("Category with id: {} not found", id);
            throw new EntityNotFoundException(CATEGORY_NOT_FOUND_ERROR_CODE, CATEGORY_NOT_FOUND_ERROR_MESSAGE);
        }
        return categoryOptional.get();
    }

    @Transactional
    public CategoryEntity getCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts(UUID id) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findByIdWithFetchedAttributesAndImagesAndProducts(id);
        if (categoryOptional.isEmpty()) {
            log.error("Category with id: {} not found", id);
            throw new EntityNotFoundException(CATEGORY_NOT_FOUND_ERROR_CODE, CATEGORY_NOT_FOUND_ERROR_MESSAGE);
        }
        return categoryOptional.get();
    }
}
