package com.deye.web.service.impl;

import com.deye.web.controller.dto.CreateCategoryDto;
import com.deye.web.controller.dto.UpdateCategoryDto;
import com.deye.web.controller.view.CategoryView;
import com.deye.web.entity.CategoryEntity;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.listeners.events.DeletedCategoryEvent;
import com.deye.web.listeners.events.SavedCategoryEvent;
import com.deye.web.mapper.CategoryMapper;
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

import static com.deye.web.utils.error.ErrorCodeUtils.CATEGORY_NOT_FOUND_ERROR_CODE;
import static com.deye.web.utils.error.ErrorMessageUtils.CATEGORY_NOT_FOUND_ERROR_MESSAGE;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {
    private final ApplicationEventPublisher applicationEventPublisher;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    /**
     * Method for creating category to add new product type
     *
     * @param categoryDto - category parameters
     */
    @Transactional
    public void create(CreateCategoryDto categoryDto) {
        log.info("Creating category: name - {}, description - {} and image - {}", categoryDto.getName(), categoryDto.getDescription(), categoryDto.getImage().getName());
        MultipartFile image = categoryDto.getImage();
        CategoryEntity category = new CategoryEntity();
        category.setName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setImage(image.getOriginalFilename());
        categoryRepository.save(category);
        applicationEventPublisher.publishEvent(new SavedCategoryEvent(category, image));
    }

    @Transactional
    public Set<CategoryView> getAll() {
        List<CategoryEntity> categories = categoryRepository.findAll();
        return categories.stream()
                .map(categoryMapper::toCategoryView)
                .collect(Collectors.toSet());
    }

    @Transactional
    public CategoryView getById(UUID id) {
        log.info("Getting category by id: {}", id);
        CategoryEntity category = getCategoryEntityById(id);
        log.info("Category found: {}", category);
        return categoryMapper.toCategoryView(category);
    }

    @Transactional
    public void deleteById(UUID id) {
        log.info("Deleting category by id: {}", id);
        CategoryEntity category = getCategoryEntityById(id);
        log.info("Category with id: {} found", id);
        categoryRepository.delete(category);
        applicationEventPublisher.publishEvent(new DeletedCategoryEvent(id, category.getImage().getName()));
    }

    @Transactional
    public void update(UUID id, UpdateCategoryDto categoryDto) {
        log.info("Updating category with id: {}", id);
        CategoryEntity category = getCategoryEntityById(id);
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
            log.info("Category new name is set");
        }
        if (categoryDto.getDescription() != null) {
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
        categoryRepository.save(category);
        applicationEventPublisher.publishEvent(new SavedCategoryEvent(category, newImage, previousImageName));
    }

    @Transactional
    public CategoryEntity getCategoryEntityById(UUID id) {
        Optional<CategoryEntity> categoryOptional = categoryRepository.findById(id);
        if (categoryOptional.isEmpty()) {
            log.error("Category with id: {} not found", id);
            throw new EntityNotFoundException(CATEGORY_NOT_FOUND_ERROR_CODE, CATEGORY_NOT_FOUND_ERROR_MESSAGE);
        }
        return categoryOptional.get();
    }
}
