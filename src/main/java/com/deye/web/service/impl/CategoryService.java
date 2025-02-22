package com.deye.web.service.impl;

import com.deye.web.controller.dto.CategoryAttributeDto;
import com.deye.web.controller.dto.CreateCategoryDto;
import com.deye.web.controller.dto.UpdateCategoryDto;
import com.deye.web.controller.view.CategoryView;
import com.deye.web.entity.AttributeEntity;
import com.deye.web.entity.CategoryAttributeEntity;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.exception.TransactionConsistencyException;
import com.deye.web.listener.events.DeletedCategoryEvent;
import com.deye.web.listener.events.SavedCategoryEvent;
import com.deye.web.mapper.CategoryMapper;
import com.deye.web.repository.AttributeRepository;
import com.deye.web.repository.CategoryRepository;
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
    private final AttributeRepository attributeRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Method for creating category to add new product type
     *
     * @param categoryDto - category parameters
     */
    @Transactional(rollbackFor = TransactionConsistencyException.class)
    public void create(CreateCategoryDto categoryDto) {
        MultipartFile image = categoryDto.getImage();
        log.info("Creating category: name - {}, description - {} and image - {}", categoryDto.getName(), categoryDto.getDescription(), image.getOriginalFilename());
        CategoryEntity category = createCategory(categoryDto);
        category = categoryRepository.saveAndFlush(category);
        applicationEventPublisher.publishEvent(new SavedCategoryEvent(category, image));
    }

    private CategoryEntity createCategory(CreateCategoryDto categoryDto) {
        CategoryEntity category = new CategoryEntity();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setImage(categoryDto.getImage().getOriginalFilename());
        addAttributesToCategory(category, categoryDto.getAttributes());
        return categoryRepository.saveAndFlush(category);
    }

    @Transactional
    public Set<CategoryView> getAll() {
        List<CategoryEntity> categories = categoryRepository.findAllWithFetchedAttributesAndImage();
        return categories.stream()
                .map(categoryMapper::toCategoryView)
                .collect(Collectors.toSet());
    }

    @Transactional
    public CategoryView getById(UUID id) {
        log.info("Getting category by id: {}", id);
        CategoryEntity category = getCategoryEntityByIdWithFetchedAttributesInformationAndImage(id);
        log.info("Category found: {}", id);
        return categoryMapper.toCategoryView(category);
    }

    //TODO: n+1 with products. Fix it
    @Transactional(rollbackFor = TransactionConsistencyException.class)
    public void deleteById(UUID id) {
        log.info("Deleting category by id: {}", id);
        CategoryEntity category = getCategoryEntityByIdWithFetchedAttributesInformationAndImage(id);
        log.info("Category with id: {} found", id);
        categoryRepository.delete(category);
        applicationEventPublisher.publishEvent(new DeletedCategoryEvent(id, category.getImage().getName()));
    }

    @Transactional(rollbackFor = TransactionConsistencyException.class)
    public void update(UUID id, UpdateCategoryDto categoryDto) {
        log.info("Updating category with id: {}", id);
        CategoryEntity category = getCategoryEntityByIdWithFetchedAttributesInformationAndImage(id);
        if (categoryDto.getName() != null && !categoryDto.getName().equals(category.getName())) {
            category.setName(categoryDto.getName());
            log.info("Category new name is set");
        }
        if (categoryDto.getDescription() != null && categoryDto.getDescription().equals(category.getName())) {
            category.setDescription(categoryDto.getDescription());
            log.info("Category new description is set");
        }
        MultipartFile newImage = categoryDto.getImage();
        String previousImageName = "";
        if (newImage != null) {
            previousImageName = category.getImage().getName();
            category.setImage(newImage.getOriginalFilename());
            log.info("Category new image is set");
        }
        List<CategoryAttributeDto> categoryAttributesToSave = categoryDto.getAttributesToSave();
        if (categoryAttributesToSave != null && !categoryAttributesToSave.isEmpty()) {
            addAttributesToCategory(category, categoryAttributesToSave);
        }
        List<UUID> attributesIdsToRemove = categoryDto.getAttributesIdsToRemove();
        if (attributesIdsToRemove != null && !attributesIdsToRemove.isEmpty()) {
            List<CategoryAttributeEntity> categoryAttributesToRemove = category.getCategoryAttributes().stream()
                    .filter(categoryAttribute -> attributesIdsToRemove.contains(categoryAttribute.getAttribute().getId()))
                    .toList();
            category.getCategoryAttributes().removeAll(categoryAttributesToRemove);
        }
        categoryRepository.saveAndFlush(category);
        applicationEventPublisher.publishEvent(new SavedCategoryEvent(category, newImage, previousImageName));
    }

    private void addAttributesToCategory(CategoryEntity category, List<CategoryAttributeDto> attributesDto) {
        log.info("Adding attributes to category: {}", category.getName());
        if (attributesDto != null && !attributesDto.isEmpty()) {
            List<UUID> attributesIds = attributesDto.stream()
                    .map(CategoryAttributeDto::getId)
                    .toList();

            List<AttributeEntity> attributes = attributeRepository.findAllById(attributesIds);

            for (AttributeEntity attribute : attributes) {
                CategoryAttributeDto attributeDto = attributesDto.stream()
                        .filter(attrDto -> attrDto.getId().equals(attribute.getId()))
                        .findAny()
                        .get();
                CategoryAttributeEntity categoryAttribute = new CategoryAttributeEntity();
                categoryAttribute.setCategory(category);
                categoryAttribute.setAttribute(attribute);
                categoryAttribute.setFilterable(attributeDto.getIsFilterable());
                categoryAttribute.setRequired(attributeDto.getIsRequired());
                category.addAttribute(categoryAttribute);
            }
            log.info("Added attributes to category: {}", category.getName());
        }
    }

    @Transactional
    public CategoryEntity getCategoryEntityById(UUID id) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            log.error("Category with id: {} was not found", id);
            throw new EntityNotFoundException(CATEGORY_NOT_FOUND_ERROR_CODE, CATEGORY_NOT_FOUND_ERROR_MESSAGE);
        }
        return categoryOptional.get();
    }

    private CategoryEntity getCategoryEntityByIdWithFetchedAttributesInformationAndImage(UUID id) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findByIdWithFetchedAttributesAndImage(id);
        if (categoryOptional.isEmpty()) {
            log.error("Category with id: {} not found", id);
            throw new EntityNotFoundException(CATEGORY_NOT_FOUND_ERROR_CODE, CATEGORY_NOT_FOUND_ERROR_MESSAGE);
        }
        return categoryOptional.get();
    }
}
