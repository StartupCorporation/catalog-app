package com.deye.web.unit.service;

import com.deye.web.async.listener.transactions.events.DeletedCategoryEvent;
import com.deye.web.async.listener.transactions.events.SavedCategoryEvent;
import com.deye.web.controller.dto.CreateCategoryDto;
import com.deye.web.controller.dto.CreateImageDto;
import com.deye.web.controller.dto.UpdateCategoryDto;
import com.deye.web.controller.dto.response.CategoryAttributeResponseDto;
import com.deye.web.controller.dto.response.CategoryResponseDto;
import com.deye.web.controller.dto.response.ImageResponseDto;
import com.deye.web.entity.*;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.repository.CategoryRepository;
import com.deye.web.service.CategoryAttributeService;
import com.deye.web.service.CategoryService;
import com.deye.web.service.FileService;
import com.deye.web.service.ProductAttributeService;
import com.deye.web.util.mapper.CategoryMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.deye.web.util.error.ErrorCodeUtils.CATEGORY_NOT_FOUND_ERROR_CODE;
import static com.deye.web.util.error.ErrorMessageUtils.CATEGORY_NOT_FOUND_ERROR_MESSAGE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {


    @Mock
    private CategoryRepository categoryRepository;


    @Mock
    private CategoryMapper categoryMapper;


    @InjectMocks
    private CategoryService categoryService;


    @Mock
    private ApplicationEventPublisher applicationEventPublisher;


    @Mock
    private FileService fileService;


    @Mock
    private ProductAttributeService productAttributeService;


    @Mock
    private CategoryAttributeService categoryAttributeService;


    @Captor
    private ArgumentCaptor<SavedCategoryEvent> eventCaptor;


    /**
     * Test to verify that getAll returns an empty set when the repository returns no categories.
     */
    @Test
    void testGetAllReturnsEmptySetWhenNoCategoriesFound() {
        // Arrange: Mock the repository to return an empty list
        when(categoryRepository.findAllWithFetchedAttributesAndImage()).thenReturn(Collections.emptyList());

        // Act: Call the method under test
        Set<CategoryResponseDto> result = categoryService.getAll();

        // Assert: Verify that the result is an empty set
        assertEquals(Collections.emptySet(), result);
        verify(categoryRepository, times(1)).findAllWithFetchedAttributesAndImage();
        verifyNoInteractions(categoryMapper);
    }


    /**
     * Test to verify that getAll maps and returns categories correctly.
     */
    @Test
    void testGetAllMapsAndReturnsCategories() {
        // Arrange: Create a mock category entity and response DTO
        CategoryEntity mockCategoryEntity = new CategoryEntity();

        // Create mock objects for the constructor
        UUID categoryId = UUID.randomUUID();
        String name = "Test Category";
        String description = "Test Description";
        ImageResponseDto imageResponseDto = new ImageResponseDto(); // Assuming a no-arg constructor or mock
        List<CategoryAttributeResponseDto> attributes = Collections.emptyList(); // Assuming an empty list for simplicity

        CategoryResponseDto mockCategoryResponseDto = new CategoryResponseDto(
                categoryId, name, description, imageResponseDto, attributes
        );

        // Mock the repository to return a list with one category entity
        when(categoryRepository.findAllWithFetchedAttributesAndImage()).thenReturn(List.of(mockCategoryEntity));

        // Mock the mapper to convert the entity to a response DTO
        when(categoryMapper.toCategoryView(mockCategoryEntity)).thenReturn(mockCategoryResponseDto);

        // Act: Call the method under test
        Set<CategoryResponseDto> result = categoryService.getAll();

        // Assert: Verify that the result contains the mapped category response DTO
        assertEquals(Set.of(mockCategoryResponseDto), result);
        verify(categoryRepository, times(1)).findAllWithFetchedAttributesAndImage();
        verify(categoryMapper, times(1)).toCategoryView(mockCategoryEntity);
    }

    // Test case for successful deletion
    @Test
    void testDeleteById_Success() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryEntity category = new CategoryEntity();
        category.setId(categoryId);
        category.setProducts(Collections.singletonList(new ProductEntity()));

        Set<FileEntity> fileEntities = new HashSet<>();
        when(categoryRepository.findByIdWithFetchedAttributesAndImagesAndProducts(categoryId))
                .thenReturn(Optional.of(category));
        when(fileService.getAllFilesByCategory(category)).thenReturn(fileEntities);
        when(fileService.getDirectoriesWithFilesNames(fileEntities)).thenReturn(new HashMap<>());

        // Act
        categoryService.deleteById(categoryId);

        // Assert
        verify(categoryRepository).delete(category);
        verify(applicationEventPublisher).publishEvent(any(DeletedCategoryEvent.class));
    }

    // Test case for category not found
    @Test
    void testDeleteById_CategoryNotFound() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        when(categoryRepository.findByIdWithFetchedAttributesAndImagesAndProducts(categoryId))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> {
            categoryService.deleteById(categoryId);
        });

        verify(categoryRepository, never()).delete(any());
        verify(applicationEventPublisher, never()).publishEvent(any());
    }

    // Test updating category name and description
    @Test
    public void testUpdateCategoryNameAndDescription() {
        UUID categoryId = UUID.randomUUID();
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(categoryId);
        categoryEntity.setName("Old Name");
        categoryEntity.setDescription("Old Description");

        when(categoryRepository.findByIdWithFetchedAttributesAndImagesAndProducts(categoryId))
                .thenReturn(Optional.of(categoryEntity));

        UpdateCategoryDto updateDto = new UpdateCategoryDto();
        updateDto.setName("New Name");
        updateDto.setDescription("New Description");

        categoryService.update(categoryId, updateDto);

        assertEquals("New Name", categoryEntity.getName());
        assertEquals("New Description", categoryEntity.getDescription());
        verify(categoryRepository).saveAndFlush(categoryEntity);
        verify(applicationEventPublisher).publishEvent(any(SavedCategoryEvent.class));
    }

    // Test updating category image
    @Test
    public void testUpdateCategoryImage() {
        UUID categoryId = UUID.randomUUID();
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(categoryId);
        FileEntity fileEntity = new FileEntity("oldImage.png", "/images");
        categoryEntity.setImage(fileEntity);

        when(categoryRepository.findByIdWithFetchedAttributesAndImagesAndProducts(categoryId))
                .thenReturn(Optional.of(categoryEntity));

        MultipartFile newImage = mock(MultipartFile.class);
        when(fileService.extractFileNameFromFile(newImage)).thenReturn("newImage.png");

        UpdateCategoryDto updateDto = new UpdateCategoryDto();
        updateDto.setImage(newImage);

        categoryService.update(categoryId, updateDto);

        assertEquals("newImage.png", fileEntity.getName());
        verify(categoryRepository).saveAndFlush(categoryEntity);
        verify(applicationEventPublisher).publishEvent(any(SavedCategoryEvent.class));
    }

    // Test removing category attributes
    @Test
    public void testRemoveCategoryAttributes() {
        UUID categoryId = UUID.randomUUID();
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(categoryId);
        categoryEntity.setProducts(Collections.singletonList(new ProductEntity()));

        AttributeEntity attribute1 = new AttributeEntity();
        attribute1.setId(UUID.randomUUID());

        CategoryAttributeEntity categoryAttribute1 = new CategoryAttributeEntity();
        categoryAttribute1.setAttribute(attribute1);

        categoryEntity.setCategoryAttributes(new HashSet<>(Collections.singletonList(categoryAttribute1)));

        when(categoryRepository.findByIdWithFetchedAttributesAndImagesAndProducts(categoryId))
                .thenReturn(Optional.of(categoryEntity));

        UpdateCategoryDto updateDto = new UpdateCategoryDto();
        updateDto.setAttributesIdsToRemove(Collections.singletonList(attribute1.getId()));

        categoryService.update(categoryId, updateDto);

        assertTrue(categoryEntity.getCategoryAttributes().isEmpty());
        verify(productAttributeService).removeAttributeValue(any(), eq(attribute1));
        verify(categoryRepository).saveAndFlush(categoryEntity);
        verify(applicationEventPublisher).publishEvent(any(SavedCategoryEvent.class));
    }

    // Test no updates needed
    @Test
    public void testNoUpdatesNeeded() {
        UUID categoryId = UUID.randomUUID();
        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(categoryId);
        categoryEntity.setName("Same Name");
        categoryEntity.setDescription("Same Description");

        when(categoryRepository.findByIdWithFetchedAttributesAndImagesAndProducts(categoryId))
                .thenReturn(Optional.of(categoryEntity));

        UpdateCategoryDto updateDto = new UpdateCategoryDto();
        updateDto.setName("Same Name");
        updateDto.setDescription("Same Description");

        categoryService.update(categoryId, updateDto);

        verify(categoryRepository).saveAndFlush(categoryEntity);
        verify(applicationEventPublisher).publishEvent(any(SavedCategoryEvent.class));
    }

    // Test category not found
    @Test
    public void testCategoryNotFound() {
        UUID categoryId = UUID.randomUUID();

        when(categoryRepository.findByIdWithFetchedAttributesAndImagesAndProducts(categoryId))
                .thenReturn(Optional.empty());

        UpdateCategoryDto updateDto = new UpdateCategoryDto();

        assertThrows(EntityNotFoundException.class, () -> categoryService.update(categoryId, updateDto));
    }

    // Test when the category is found
    @Test
    void testGetCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts_Found() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryEntity mockCategoryEntity = new CategoryEntity();
        when(categoryRepository.findByIdWithFetchedAttributesAndImagesAndProducts(categoryId))
                .thenReturn(Optional.of(mockCategoryEntity));

        // Act
        CategoryEntity result = categoryService.getCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts(categoryId);

        // Assert
        assertNotNull(result, "The result should not be null when the category is found.");
        assertEquals(mockCategoryEntity, result, "The returned category entity should match the mock entity.");
        verify(categoryRepository, times(1)).findByIdWithFetchedAttributesAndImagesAndProducts(categoryId);
    }

    // Test when the category is not found
    @Test
    void testGetCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts_NotFound() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        when(categoryRepository.findByIdWithFetchedAttributesAndImagesAndProducts(categoryId))
                .thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () ->
                        categoryService.getCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts(categoryId),
                "An EntityNotFoundException should be thrown when the category is not found.");

        assertEquals(CATEGORY_NOT_FOUND_ERROR_CODE, exception.getCode(), "The error code should match the expected value.");
        assertEquals(CATEGORY_NOT_FOUND_ERROR_MESSAGE, exception.getMessage(), "The error message should match the expected value.");
        verify(categoryRepository, times(1)).findByIdWithFetchedAttributesAndImagesAndProducts(categoryId);
    }


    /**
     * Test case for successfully retrieving a category by ID.
     * Verifies that the category is correctly fetched and mapped to a response DTO.
     */
    @Test
    void testGetById_Success() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryEntity categoryEntity = new CategoryEntity();
        ImageResponseDto imageResponseDto = new ImageResponseDto(); // Assuming a default constructor or mock
        CategoryResponseDto categoryResponseDto = new CategoryResponseDto(
                categoryId,
                "Category Name",
                "Category Description",
                imageResponseDto,
                Collections.emptyList()
        );

        when(categoryRepository.findByIdWithFetchedAttributesAndImage(categoryId)).thenReturn(Optional.of(categoryEntity));
        when(categoryMapper.toCategoryView(categoryEntity)).thenReturn(categoryResponseDto);

        // Act
        CategoryResponseDto result = categoryService.getById(categoryId);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(categoryResponseDto, result, "The result should match the expected category response DTO");
        verify(categoryRepository, times(1)).findByIdWithFetchedAttributesAndImage(categoryId);
        verify(categoryMapper, times(1)).toCategoryView(categoryEntity);
    }


    /**
     * Test case for handling the scenario where the category is not found.
     * Verifies that an EntityNotFoundException is thrown with the correct error code and message.
     */
    @Test
    void testGetById_CategoryNotFound() {
        // Arrange
        UUID categoryId = UUID.randomUUID();

        when(categoryRepository.findByIdWithFetchedAttributesAndImage(categoryId)).thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            categoryService.getById(categoryId);
        });

        assertEquals(CATEGORY_NOT_FOUND_ERROR_MESSAGE, exception.getMessage(), "The error message should match the expected value");
        verify(categoryRepository, times(1)).findByIdWithFetchedAttributesAndImage(categoryId);
        verifyNoInteractions(categoryMapper);
    }

    @Test
    void testCreateCategorySuccessfully() {
        // Arrange
        CreateCategoryDto categoryDto = new CreateCategoryDto();
        categoryDto.setName("Electronics");
        categoryDto.setDescription("All kinds of electronic items");
        MultipartFile image = mock(MultipartFile.class);
        when(image.getOriginalFilename()).thenReturn("image.png");
        categoryDto.setImage(image);
        categoryDto.setAttributes(List.of()); // Assuming no attributes for simplicity

        FileEntity fileEntity = new FileEntity();
        when(fileService.createFileEntity(image)).thenReturn(fileEntity);

        CategoryEntity savedCategory = new CategoryEntity();
        savedCategory.setId(UUID.randomUUID());
        savedCategory.setName(categoryDto.getName());
        savedCategory.setDescription(categoryDto.getDescription());
        savedCategory.setImage(fileEntity);
        when(categoryRepository.saveAndFlush(any(CategoryEntity.class))).thenReturn(savedCategory);

        // Act
        categoryService.create(categoryDto);

        // Assert
        verify(categoryRepository).saveAndFlush(any(CategoryEntity.class));
        verify(categoryAttributeService).addAttributesToCategory(any(CategoryEntity.class), eq(categoryDto.getAttributes()));
        verify(applicationEventPublisher).publishEvent(eventCaptor.capture());

        SavedCategoryEvent savedCategoryEvent = eventCaptor.getValue();
        assertEquals(savedCategory, savedCategoryEvent.getCategory());

        // Assuming CreateImageDto has a constructor or fields for image and fileEntity
        CreateImageDto expectedImageDto = new CreateImageDto(image, fileEntity);
        assertEquals(expectedImageDto.getImage(), savedCategoryEvent.getCreateImageDto().getImage());
        assertEquals(expectedImageDto.getFileName(), savedCategoryEvent.getCreateImageDto().getFileName());
    }
}