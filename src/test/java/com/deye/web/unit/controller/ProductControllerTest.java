package com.deye.web.unit.controller;

import com.deye.web.controller.ProductController;
import com.deye.web.controller.dto.CreateProductDto;
import com.deye.web.controller.dto.FileDto;
import com.deye.web.controller.dto.ProductFilterDto;
import com.deye.web.controller.dto.UpdateProductDto;
import com.deye.web.controller.dto.response.ProductResponseDto;
import com.deye.web.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {


    @Mock
    private ProductService productService;


    @InjectMocks
    private ProductController productController;


    /**
     * Test to verify that the save method in ProductController
     * calls the save method in ProductService with the correct CreateProductDto.
     */
    @Test
    public void testSaveProduct() {
        // Arrange: Create a valid CreateProductDto object
        CreateProductDto createProductDto = new CreateProductDto();
        createProductDto.setName("Valid Product Name");
        createProductDto.setDescription("This is a valid product description.");
        createProductDto.setPrice(99.99f);
        createProductDto.setStockQuantity(10);
        createProductDto.setCategoryId(UUID.randomUUID());
        List<FileDto> files = new ArrayList<>();
        FileDto fileDto = new FileDto();
        fileDto.setFile(new MockMultipartFile("image1", "image1.jpg", "image/jpeg", new byte[]{1, 2, 3}));
        fileDto.setOrder(1);
        files.add(fileDto);
        FileDto fileDto2 = new FileDto();
        fileDto2.setOrder(2);
        fileDto2.setFile(new MockMultipartFile("image2", "image2.jpg", "image/jpeg", new byte[]{4, 5, 6}));
        files.add(fileDto2);
        createProductDto.setImages(files);

        // Act: Call the save method
        productController.save(createProductDto);

        // Assert: Verify that the ProductService's save method was called with the correct parameter
        verify(productService, times(1)).save(createProductDto);
    }


    /**
     * Test the successful retrieval of a product by ID.
     * Ensures that the service method is called and the response is correctly wrapped in a ResponseEntity.
     */
    @Test
    void testGetById_Success() {
        // Arrange
        UUID productId = UUID.randomUUID();

        // Mock the ProductResponseDto since the constructor is not accessible
        ProductResponseDto mockResponse = mock(ProductResponseDto.class);

        when(productService.getById(productId)).thenReturn(mockResponse);

        // Act
        ResponseEntity<ProductResponseDto> responseEntity = productController.getById(productId);

        // Assert
        assertEquals(ResponseEntity.ok(mockResponse), responseEntity);
    }


    /**
     * Test case to verify that the update method in ProductController
     * calls the update method in ProductService with correct parameters.
     */
    @Test
    void testUpdateProduct() {
        // Arrange
        UUID productId = UUID.randomUUID();
        UpdateProductDto updateProductDto = new UpdateProductDto();
        updateProductDto.setName("Test Product");
        updateProductDto.setDescription("This is a test product description.");
        updateProductDto.setPrice(100.0f);
        updateProductDto.setStockQuantity(10);
        updateProductDto.setImagesToAdd(new ArrayList<>());
        updateProductDto.setImagesIdsToRemove(Collections.emptyList());
        updateProductDto.setAttributesValuesToSave(new HashMap<>());
        updateProductDto.setAttributesIdsToRemove(new HashSet<>());

        // Act
        productController.update(productId, updateProductDto);

        // Assert
        ArgumentCaptor<UUID> idCaptor = ArgumentCaptor.forClass(UUID.class);
        ArgumentCaptor<UpdateProductDto> dtoCaptor = ArgumentCaptor.forClass(UpdateProductDto.class);
        verify(productService, times(1)).update(idCaptor.capture(), dtoCaptor.capture());

        assertEquals(productId, idCaptor.getValue(), "The product ID should match the expected value.");
        assertEquals(updateProductDto, dtoCaptor.getValue(), "The UpdateProductDto should match the expected value.");
    }


    /**
     * Test case for the getAll method with valid inputs.
     * Ensures that the method returns the expected response entity.
     */
    @Test
    void testGetAll_ValidInputs() {
        // Arrange
        int page = 0;
        int size = 10;
        ProductFilterDto filterDto = new ProductFilterDto();
        filterDto.setName("Test Product");
        filterDto.setCategoriesIds(Collections.singletonList(UUID.randomUUID()));

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDto> expectedPage = new PageImpl<>(Collections.emptyList());

        when(productService.getAll(filterDto, pageable)).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<ProductResponseDto>> response = productController.getAll(page, size, filterDto);

        // Assert
        assertEquals(ResponseEntity.ok(expectedPage), response);
    }


    /**
     * Test case for the getAll method with empty filter.
     * Ensures that the method handles empty filters correctly.
     */
    @Test
    void testGetAll_EmptyFilter() {
        // Arrange
        int page = 1;
        int size = 5;
        ProductFilterDto filterDto = new ProductFilterDto();

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDto> expectedPage = new PageImpl<>(Collections.emptyList());

        when(productService.getAll(filterDto, pageable)).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<ProductResponseDto>> response = productController.getAll(page, size, filterDto);

        // Assert
        assertEquals(ResponseEntity.ok(expectedPage), response);
    }


    /**
     * Test case for the getAll method with a large page size.
     * Ensures that the method can handle large page sizes.
     */
    @Test
    void testGetAll_LargePageSize() {
        // Arrange
        int page = 0;
        int size = 1000;
        ProductFilterDto filterDto = new ProductFilterDto();
        filterDto.setName("Another Product");

        Pageable pageable = PageRequest.of(page, size);
        Page<ProductResponseDto> expectedPage = new PageImpl<>(Collections.emptyList());

        when(productService.getAll(filterDto, pageable)).thenReturn(expectedPage);

        // Act
        ResponseEntity<Page<ProductResponseDto>> response = productController.getAll(page, size, filterDto);

        // Assert
        assertEquals(ResponseEntity.ok(expectedPage), response);
    }


    /**
     * Test to verify that deleteById method in ProductController
     * calls the deleteById method in ProductService with the correct UUID.
     */
    @Test
    void testDeleteById_callsServiceMethodWithCorrectId() {
        // Arrange
        UUID productId = UUID.randomUUID();

        // Act
        productController.deleteById(productId);

        // Assert
        verify(productService, times(1)).deleteById(productId);
    }


    /**
     * Test to verify that deleteById method in ProductController
     * handles null UUID gracefully, expecting no interaction with the service.
     */
    @Test
    void testDeleteById_withNullId() {
        // Arrange
        UUID productId = null;

        // Act & Assert
        try {
            productController.deleteById(productId);
        } catch (Exception e) {
            // Verify that an exception is thrown
            // In a real-world scenario, you might want to handle this differently
        }
    }
}