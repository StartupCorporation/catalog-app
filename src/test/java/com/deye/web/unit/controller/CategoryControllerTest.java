package com.deye.web.unit.controller;

import com.deye.web.controller.CategoryController;
import com.deye.web.controller.dto.CreateCategoryDto;
import com.deye.web.controller.dto.UpdateCategoryDto;
import com.deye.web.controller.dto.response.CategoryResponseDto;
import com.deye.web.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class CategoryControllerTest {


    @Mock
    private CategoryService categoryService;


    @InjectMocks
    private CategoryController categoryController;


    /**
     * Test to verify that getCategory returns the correct response when a valid UUID is provided.
     */
    @Test
    void testGetCategory_Success() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        CategoryResponseDto mockResponse = new CategoryResponseDto();
        // Assume mockResponse is populated with necessary data

        when(categoryService.getById(categoryId)).thenReturn(mockResponse);

        // Act
        ResponseEntity<CategoryResponseDto> response = categoryController.getCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected HTTP status OK");
        assertEquals(mockResponse, response.getBody(), "Expected response body to match mock response");
    }


    /**
     * Test to verify that getCategory handles the scenario where the service returns null.
     * This might simulate a case where the category is not found.
     */
    @Test
    void testGetCategory_NotFound() {
        // Arrange
        UUID categoryId = UUID.randomUUID();

        when(categoryService.getById(categoryId)).thenReturn(null);

        // Act
        ResponseEntity<CategoryResponseDto> response = categoryController.getCategory(categoryId);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode(), "Expected HTTP status OK even if category is not found");
        assertEquals(null, response.getBody(), "Expected response body to be null when category is not found");
    }


    /**
     * Test class for CategoryController.
     * Ensures that the deleteCategory method correctly interacts with the CategoryService.
     */


    /**
     * Test to verify that the deleteCategory method calls the deleteById method of CategoryService
     * with the correct UUID.
     */
    @Test
    void testDeleteCategory() {
        // Arrange
        UUID categoryId = UUID.randomUUID();

        // Act
        categoryController.deleteCategory(categoryId);

        // Assert
        verify(categoryService, times(1)).deleteById(categoryId);
    }


    /**
     * Test to verify that the updateCategory method calls the update method of CategoryService with correct parameters.
     */
    @Test
    void testUpdateCategory() {
        // Arrange
        UUID categoryId = UUID.randomUUID();
        UpdateCategoryDto updateCategoryDto = new UpdateCategoryDto();
        updateCategoryDto.setName("Valid Name");
        updateCategoryDto.setDescription("Valid Description");
        updateCategoryDto.setImage(new MockMultipartFile("image", new byte[0]));
        updateCategoryDto.setAttributesIdsToRemove(List.of(UUID.randomUUID()));

        // Act
        categoryController.updateCategory(categoryId, updateCategoryDto);

        // Assert
        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UpdateCategoryDto> dtoCaptor = ArgumentCaptor.forClass(UpdateCategoryDto.class);

        verify(categoryService, times(1)).update(idCaptor.capture(), dtoCaptor.capture());

        assertEquals(categoryId, idCaptor.getValue(), "The captured UUID should match the provided categoryId.");
        assertEquals(updateCategoryDto, dtoCaptor.getValue(), "The captured UpdateCategoryDto should match the provided updateCategoryDto.");
    }


    /**
     * Test to ensure that the createCategory method in CategoryController
     * calls the create method in CategoryService with the correct parameters.
     */
    @Test
    void testCreateCategory() {
        // Arrange: Create a valid CreateCategoryDto object
        CreateCategoryDto createCategoryDto = new CreateCategoryDto();
        createCategoryDto.setName("Electronics");
        createCategoryDto.setDescription("Category for electronic items");
        MockMultipartFile image = new MockMultipartFile("image", "image.jpg", "image/jpeg", new byte[]{1, 2, 3});
        createCategoryDto.setImage(image);
        createCategoryDto.setAttributes(Collections.emptyList());

        // Act: Call the createCategory method
        categoryController.createCategory(createCategoryDto);

        // Assert: Verify that the create method in CategoryService is called with the correct parameter
        verify(categoryService).create(createCategoryDto);
    }
}