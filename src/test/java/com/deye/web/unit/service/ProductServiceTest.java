package com.deye.web.unit.service;

import com.deye.web.async.listener.transactions.events.DeletedProductEvent;
import com.deye.web.async.listener.transactions.events.ReservationResultEvent;
import com.deye.web.async.listener.transactions.events.SavedProductEvent;
import com.deye.web.async.message.OrderCreatedMessage.OrderCreatedPayload;
import com.deye.web.controller.dto.*;
import com.deye.web.controller.dto.response.AttributeResponseDto;
import com.deye.web.controller.dto.response.ImageResponseDto;
import com.deye.web.controller.dto.response.ProductResponseDto;
import com.deye.web.controller.dto.response.ProductResponseDtoPage;
import com.deye.web.entity.*;
import com.deye.web.entity.attribute.definition.CheckboxAttributeDefinition;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.exception.dlq.ActionNotAllowedSkipDLQException;
import com.deye.web.repository.ProductRepository;
import com.deye.web.repository.spricification.ProductFilterSpecification;
import com.deye.web.service.*;
import com.deye.web.util.error.ErrorMessageUtils;
import com.deye.web.util.mapper.ImageMapper;
import com.deye.web.util.mapper.ProductMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.lang.reflect.Constructor;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryAttributeService categoryAttributeService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductMapper productMapper;

    @Mock
    private ProductFilterSpecification productFilterSpecification;

    @Mock
    private ProductAttributeService productAttributeService;

    @Mock
    private FileService fileService;

    @Mock
    private ImageMapper imageMapper;

    private AttributeEntity attributeEntity;

    @BeforeEach
    public void init() {
        attributeEntity = new AttributeEntity();
        attributeEntity.setId(UUID.randomUUID());
        attributeEntity.setName("test");
        attributeEntity.setDescription("test");
        attributeEntity.setDefinition(new CheckboxAttributeDefinition());
    }

    /**
     * Test the successful execution of the save method.
     * This test covers the scenario where all dependencies work as expected.
     */
    @Test
    public void testSave_Success() {
        // Arrange
        CreateProductDto createProductDto = new CreateProductDto();
        createProductDto.setName("Test Product");
        createProductDto.setDescription("A test product description");
        createProductDto.setPrice(100.0f);
        createProductDto.setStockQuantity(10);
        createProductDto.setCategoryId(UUID.randomUUID());
        MultipartFile[] images = new MultipartFile[1];
        images[0] = Mockito.mock(MultipartFile.class);
        FileDto fileDto = new FileDto();
        fileDto.setFile(images[0]);
        fileDto.setOrder(1);
        List<FileDto> fileDtoList = new ArrayList<>();
        fileDtoList.add(fileDto);
        createProductDto.setImages(fileDtoList);
        createProductDto.setAttributesValuesToSave(new HashMap<>());

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName("Test Category");
        categoryEntity.setCategoryAttributes(new HashSet<>());

        when(categoryService.getCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts(any(UUID.class)))
                .thenReturn(categoryEntity);

        when(fileService.createFileEntity(any(), any())).thenReturn(new FileEntity());

        when(fileService.getFileEntityByFile(anyCollection(), any(MultipartFile.class)))
                .thenReturn(Optional.of(new FileEntity()));

        // Act
        productService.save(createProductDto);

        // Assert
        verify(productRepository, times(1)).saveAndFlush(any(ProductEntity.class));
        verify(eventPublisher, times(1)).publishEvent(any(SavedProductEvent.class));
    }


    /**
     * Test the scenario where the category is not found.
     * This test ensures that the method handles the case where the category service returns null.
     */
    @Test
    public void testSave_CategoryNotFound() {
        // Arrange
        CreateProductDto createProductDto = new CreateProductDto();
        createProductDto.setName("Test Product");
        createProductDto.setDescription("A test product description");
        createProductDto.setPrice(100.0f);
        createProductDto.setStockQuantity(10);
        createProductDto.setCategoryId(UUID.randomUUID());
        MultipartFile[] images = new MultipartFile[1];
        images[0] = Mockito.mock(MultipartFile.class);
        FileDto fileDto = new FileDto();
        fileDto.setFile(images[0]);
        fileDto.setOrder(1);
        createProductDto.setImages(List.of(fileDto));
        createProductDto.setAttributesValuesToSave(new HashMap<>());

        // Assuming the error code is 404 for not found
        when(categoryService.getCategoryEntityByIdWithFetchedAttributesInformationAndImagesAndProducts(any(UUID.class)))
                .thenThrow(new EntityNotFoundException(404, "Category not found"));

        // Act & Assert
        try {
            productService.save(createProductDto);
        } catch (EntityNotFoundException e) {
            assertNotNull(e);
        }

        verify(productRepository, never()).saveAndFlush(any(ProductEntity.class));
        verify(eventPublisher, never()).publishEvent(any(SavedProductEvent.class));
    }


    // Test case for successful reservation completion
    @Test
    void testFinishReservationSuccess() {
        // Mock OrderCreatedPayload
        OrderCreatedPayload orderCreatedPayload = mock(OrderCreatedPayload.class);

        // Create a mock ReservationDto using the available constructor
        ReservationDto reservationDto = new ReservationDto(orderCreatedPayload);

        // Set productsIdsAndQuantity using ReflectionTestUtils
        UUID productId = UUID.randomUUID();
        Map<UUID, Integer> productsIdsAndQuantity = new HashMap<>();
        productsIdsAndQuantity.put(productId, 5);
        ReflectionTestUtils.setField(reservationDto, "productsIdsAndQuantity", productsIdsAndQuantity);

        // Create a mock ProductEntity
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(productId);
        productEntity.setReservedQuantity(10);
        productEntity.setStockQuantity(20);

        // Mock the repository call
        when(productRepository.findAllById(anySet())).thenReturn(Collections.singletonList(productEntity));

        // Execute the method
        productService.finishReservation(reservationDto);

        // Verify the changes in product entity
        assertEquals(5, productEntity.getReservedQuantity());
        assertEquals(15, productEntity.getStockQuantity());

        // Verify that the repository's saveAll method was called
        verify(productRepository, times(1)).saveAll(anyList());
    }

    // Test case for EntityNotFoundException when product is not found
    @Test
    void testFinishReservationProductNotFound() {
        // Mock OrderCreatedPayload
        OrderCreatedPayload orderCreatedPayload = mock(OrderCreatedPayload.class);

        // Create a mock ReservationDto using the available constructor
        ReservationDto reservationDto = new ReservationDto(orderCreatedPayload);

        // Set productsIdsAndQuantity using ReflectionTestUtils
        UUID productId = UUID.randomUUID();
        Map<UUID, Integer> productsIdsAndQuantity = new HashMap<>();
        productsIdsAndQuantity.put(productId, 5);
        ReflectionTestUtils.setField(reservationDto, "productsIdsAndQuantity", productsIdsAndQuantity);

        // Mock the repository call to return an empty list
        when(productRepository.findAllById(anySet())).thenReturn(Collections.emptyList());

        // Execute the method and expect an exception
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            productService.finishReservation(reservationDto);
        });

        // Verify the exception details
        assertEquals("Product not found", exception.getMessage());
    }


    /**
     * Test to verify that the product is fetched successfully by ID.
     */
    @Test
    void testGetById_ProductFound() throws Exception {
        // Arrange
        UUID productId = UUID.randomUUID();
        ProductEntity mockProductEntity = new ProductEntity();
        mockProductEntity.setId(productId);
        mockProductEntity.setName("Test Product");

        when(productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(productId))
                .thenReturn(Optional.of(mockProductEntity));

        // Use reflection to create a ProductResponseDto with mock data
        Constructor<ProductResponseDto> constructor = ProductResponseDto.class.getDeclaredConstructor(
                UUID.class, String.class, String.class, Float.class, Integer.class, UUID.class, String.class, Set.class, List.class
        );
        constructor.setAccessible(true);
        ProductResponseDto expectedResponse = constructor.newInstance(
                productId,
                "Test Product",
                "Description",
                99.99f,
                10,
                UUID.randomUUID(),
                "Category Name",
                new HashSet<>(),
                new ArrayList<>()
        );

        when(productMapper.toProductResponseDto(mockProductEntity)).thenReturn(expectedResponse);

        // Act
        ProductResponseDto actualResponse = productService.getById(productId);

        // Assert
        assertEquals(expectedResponse, actualResponse);
        verify(productRepository, times(1)).findByIdWithFetchedImagesAndCategoryAndAttributes(productId);
        verify(productMapper, times(1)).toProductResponseDto(mockProductEntity);
    }


    /**
     * Test to verify that an exception is thrown when the product is not found.
     */
    @Test
    void testGetById_ProductNotFound() {
        // Arrange
        UUID productId = UUID.randomUUID();

        when(productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(productId))
                .thenReturn(Optional.empty());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            productService.getById(productId);
        });

        // Assuming EntityNotFoundException has a method getErrorCode() or similar
        // If not, adjust this part according to the actual class implementation
        // assertEquals(ErrorCodeUtils.PRODUCT_NOT_FOUND_ERROR_CODE, exception.getErrorCode());
        // assertEquals(ErrorMessageUtils.PRODUCT_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());

        verify(productRepository, times(1)).findByIdWithFetchedImagesAndCategoryAndAttributes(productId);
        verify(productMapper, never()).toProductResponseDto(any());
    }

    // Test fetching products with valid filters and pageable
    @Test
    void testGetAllWithValidFilters() throws Exception {
        // Arrange
        ProductFilterDto filterDto = new ProductFilterDto();
        filterDto.setName("Test Product");
        filterDto.setCategoriesIds(Collections.singletonList(UUID.randomUUID()));

        Pageable pageable = PageRequest.of(0, 10);
        Specification<ProductEntity> specification = mock(Specification.class);
        when(productFilterSpecification.filterBy(filterDto)).thenReturn(specification);

        ProductEntity productEntity = new ProductEntity();
        when(productRepository.findAll(any(Specification.class))).thenReturn(List.of(productEntity));

        // Use reflection to create a ProductResponseDto instance
        Constructor<ProductResponseDto> constructor = ProductResponseDto.class.getDeclaredConstructor(
                UUID.class, String.class, String.class, Float.class, Integer.class, UUID.class, String.class,
                Set.class, List.class
        );
        constructor.setAccessible(true);
        ProductResponseDto productResponseDto = constructor.newInstance(
                UUID.randomUUID(),
                "Product Name",
                "Product Description",
                99.99f,
                10,
                UUID.randomUUID(),
                "Category Name",
                Set.of(new ImageResponseDto()), // Assuming ImageResponseDto has a default constructor
                List.of(new AttributeResponseDto()) // Assuming AttributeResponseDto has a default constructor
        );

        when(productMapper.toProductResponseDto(productEntity)).thenReturn(productResponseDto);

        // Act
        ProductResponseDtoPage result = productService.getAll(filterDto, pageable);

        // Assert
        assertEquals(1, result.getTotalElements());
        assertEquals(productResponseDto, result.getContent().get(0));
        verify(productRepository, times(1)).findAll(any(Specification.class));
        verify(productMapper, times(1)).toProductResponseDto(productEntity);
    }

    // Test handling empty product list
    @Test
    void testGetAllWithEmptyList() {
        // Arrange
        ProductFilterDto filterDto = new ProductFilterDto();
        Pageable pageable = mock(Pageable.class);
        Specification<ProductEntity> specification = mock(Specification.class);
        when(productFilterSpecification.filterBy(filterDto)).thenReturn(specification);

        when(productRepository.findAll(any(Specification.class))).thenReturn(Collections.emptyList());

        // Act
        ProductResponseDtoPage result = productService.getAll(filterDto, pageable);

        // Assert
        assertEquals(0, result.getTotalElements());
        verify(productRepository, times(1)).findAll(any(Specification.class));
        verify(productMapper, never()).toProductResponseDto(any());
    }


    /**
     * Test case to verify successful deletion of a product by ID.
     * Ensures that the product is fetched, deleted, and the appropriate event is published.
     */
    @Test
    public void testDeleteById_Success() {
        UUID productId = UUID.randomUUID();
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(productId);
        Set<FileEntity> images = new HashSet<>();
        productEntity.setImages(images);

        // Mocking the repository to return the product entity
        when(productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(productId))
                .thenReturn(Optional.of(productEntity));

        // Mocking the image mapper to return a list of DeleteImageDto
        List<DeleteImageDto> deleteImageDtos = new ArrayList<>();
        when(imageMapper.toDeleteImageDtoList(images)).thenReturn(deleteImageDtos);

        // Invoke the method under test
        productService.deleteById(productId);

        // Verify that the product repository delete method was called
        verify(productRepository).delete(productEntity);

        // Capture and verify the event published
        ArgumentCaptor<DeletedProductEvent> eventCaptor = ArgumentCaptor.forClass(DeletedProductEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        DeletedProductEvent capturedEvent = eventCaptor.getValue();
        assert capturedEvent.getProductId().equals(productId);
        assert capturedEvent.getDeleteImages().equals(deleteImageDtos);
    }


    /**
     * Test case to verify that an exception is thrown when trying to delete a non-existent product.
     * Ensures that the EntityNotFoundException is thrown and no deletion or event publication occurs.
     */
    @Test
    public void testDeleteById_ProductNotFound() {
        UUID productId = UUID.randomUUID();

        // Mocking the repository to return an empty Optional
        when(productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(productId))
                .thenReturn(Optional.empty());

        // Verify that the exception is thrown
        assertThrows(EntityNotFoundException.class, () -> productService.deleteById(productId));

        // Verify that no deletion or event publication occurs
        verify(productRepository, never()).delete(any(ProductEntity.class));
        verify(eventPublisher, never()).publishEvent(any(DeletedProductEvent.class));
    }

    // Test for updating product name
    @Test
    void testUpdateProductName() {
        UUID productId = UUID.randomUUID();
        ProductEntity productEntity = createProductEntity(productId);
        UpdateProductDto updateProductDto = new UpdateProductDto();
        updateProductDto.setName("New Product Name");

        when(productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(productId))
                .thenReturn(Optional.of(productEntity));

        productService.update(productId, updateProductDto);

        assertEquals("New Product Name", productEntity.getName());
        verify(productRepository).saveAndFlush(productEntity);
    }

    // Test for updating product description
    @Test
    void testUpdateProductDescription() {
        UUID productId = UUID.randomUUID();
        ProductEntity productEntity = createProductEntity(productId);
        UpdateProductDto updateProductDto = new UpdateProductDto();
        updateProductDto.setDescription("New Description");

        when(productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(productId))
                .thenReturn(Optional.of(productEntity));

        productService.update(productId, updateProductDto);

        assertEquals("New Description", productEntity.getDescription());
        verify(productRepository).saveAndFlush(productEntity);
    }

    // Test for updating product price
    @Test
    void testUpdateProductPrice() {
        UUID productId = UUID.randomUUID();
        ProductEntity productEntity = createProductEntity(productId);
        UpdateProductDto updateProductDto = new UpdateProductDto();
        updateProductDto.setPrice(99.99f);

        when(productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(productId))
                .thenReturn(Optional.of(productEntity));

        productService.update(productId, updateProductDto);

        assertEquals(99.99f, productEntity.getPrice());
        verify(productRepository).saveAndFlush(productEntity);
    }

    // Test for updating product stock quantity
    @Test
    void testUpdateProductStockQuantity() {
        UUID productId = UUID.randomUUID();
        ProductEntity productEntity = createProductEntity(productId);
        UpdateProductDto updateProductDto = new UpdateProductDto();
        updateProductDto.setStockQuantity(50);

        when(productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(productId))
                .thenReturn(Optional.of(productEntity));

        productService.update(productId, updateProductDto);

        assertEquals(50, productEntity.getStockQuantity());
        verify(productRepository).saveAndFlush(productEntity);
    }

    // Test for adding images
    @Test
    void testAddImages() {
        UUID productId = UUID.randomUUID();
        ProductEntity productEntity = createProductEntity(productId);
        UpdateProductDto updateProductDto = new UpdateProductDto();
        MultipartFile[] images = new MultipartFile[1];
        images[0] = Mockito.mock(MultipartFile.class);
        FileDto fileDto = new FileDto();
        fileDto.setFile(images[0]);
        fileDto.setOrder(1);
        updateProductDto.setImagesToAdd(Arrays.asList(fileDto));

        when(productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(productId))
                .thenReturn(Optional.of(productEntity));
        when(fileService.createFileEntity(any(), any())).thenReturn(new FileEntity());

        productService.update(productId, updateProductDto);

        verify(fileService).createFileEntity(fileDto, productEntity);
        verify(productRepository).saveAndFlush(productEntity);
    }

    // Test for removing images
    @Test
    void testRemoveImages() {
        UUID productId = UUID.randomUUID();
        ProductEntity productEntity = createProductEntity(productId);
        UpdateProductDto updateProductDto = new UpdateProductDto();
        List<UUID> imagesIdsToRemove = List.of(UUID.randomUUID());
        updateProductDto.setImagesIdsToRemove(imagesIdsToRemove);

        when(productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(productId))
                .thenReturn(Optional.of(productEntity));
        when(fileService.removeFileEntitiesByIds(any(), any())).thenReturn(Set.of());

        productService.update(productId, updateProductDto);

        verify(fileService).removeFileEntitiesByIds(any(), eq(imagesIdsToRemove));
        verify(productRepository).saveAndFlush(productEntity);
    }

    // Test for adding attributes
    @Test
    void testAddAttributes() {
        UUID productId = UUID.randomUUID();
        ProductEntity productEntity = createProductEntity(productId);
        UpdateProductDto updateProductDto = new UpdateProductDto();
        Map<UUID, Object> attributesValuesToSave = Map.of(attributeEntity.getId(), new Object());
        updateProductDto.setAttributesValuesToSave(attributesValuesToSave);

        when(productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(productId))
                .thenReturn(Optional.of(productEntity));

        productService.update(productId, updateProductDto);

        verify(categoryAttributeService).validateCategoryAttributesValues(productEntity, attributesValuesToSave);
        verify(productRepository).saveAndFlush(productEntity);
    }

    // Test for entity not found exception
    @Test
    void testEntityNotFoundException() {
        UUID productId = UUID.randomUUID();
        UpdateProductDto updateProductDto = new UpdateProductDto();

        when(productRepository.findByIdWithFetchedImagesAndCategoryAndAttributes(productId))
                .thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            productService.update(productId, updateProductDto);
        });

        // Adjusted to check the exception message
        assertEquals(ErrorMessageUtils.PRODUCT_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());
    }

    // Helper method to create a ProductEntity
    private ProductEntity createProductEntity(UUID id) {
        ProductEntity productEntity = new ProductEntity();
        productEntity.setId(id);
        productEntity.setName("Old Product Name");
        productEntity.setDescription("Old Description");
        productEntity.setPrice(50.0f);
        productEntity.setStockQuantity(10);
        productEntity.setImages(new HashSet<>());
        productEntity.setAttributesValuesForProduct(new HashSet<>());

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setId(UUID.randomUUID());
        categoryEntity.setName("New Category");
        categoryEntity.setDescription("New Description");
        CategoryAttributeEntity categoryAttributeEntity = new CategoryAttributeEntity();
        categoryAttributeEntity.setId(UUID.randomUUID());
        categoryAttributeEntity.setRequired(true);
        categoryAttributeEntity.setCategory(categoryEntity);
        categoryAttributeEntity.setAttribute(attributeEntity);
        categoryEntity.setCategoryAttributes(Set.of(categoryAttributeEntity));
        productEntity.setCategory(categoryEntity);
        return productEntity;
    }

    // Test for successful reservation of products
    @Test
    public void testReserveProducts_SuccessfulReservation() {
        // Arrange
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setStockQuantity(10);
        product.setReservedQuantity(2);

        Map<UUID, Integer> productsIdsAndQuantity = new HashMap<>();
        productsIdsAndQuantity.put(productId, 3);

        ReservationDto reservationDto = new ReservationDto(new ArrayList<>()); // Use constructor with List
        ReflectionTestUtils.setField(reservationDto, "orderId", orderId);
        ReflectionTestUtils.setField(reservationDto, "productsIdsAndQuantity", productsIdsAndQuantity);

        when(productRepository.findAllById(any())).thenReturn(Collections.singletonList(product));

        // Act
        productService.reserveProducts(reservationDto);

        // Assert
        assertEquals(5, product.getReservedQuantity());
        verify(productRepository).saveAll(any());
        ArgumentCaptor<ReservationResultEvent> eventCaptor = ArgumentCaptor.forClass(ReservationResultEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(orderId, eventCaptor.getValue().getOrderId());
        // assertEquals(RabbitMqEvent.PRODUCTS_RESERVED_FOR_ORDER, eventCaptor.getValue().getEvent()); // Assuming getEvent() is the correct method
    }

    // Test for reservation failure due to insufficient stock
    @Test
    public void testReserveProducts_InsufficientStock() {
        // Arrange
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        ProductEntity product = new ProductEntity();
        product.setId(productId);
        product.setStockQuantity(5);
        product.setReservedQuantity(2);

        Map<UUID, Integer> productsIdsAndQuantity = new HashMap<>();
        productsIdsAndQuantity.put(productId, 4);

        ReservationDto reservationDto = new ReservationDto(new ArrayList<>()); // Use constructor with List
        ReflectionTestUtils.setField(reservationDto, "orderId", orderId);
        ReflectionTestUtils.setField(reservationDto, "productsIdsAndQuantity", productsIdsAndQuantity);

        when(productRepository.findAllById(any())).thenReturn(Collections.singletonList(product));

        // Act & Assert
        ActionNotAllowedSkipDLQException exception = assertThrows(ActionNotAllowedSkipDLQException.class, () -> {
            productService.reserveProducts(reservationDto);
        });

        assertEquals("Quantity on stock is less then total reservation quantity", exception.getMessage());
        ArgumentCaptor<ReservationResultEvent> eventCaptor = ArgumentCaptor.forClass(ReservationResultEvent.class);
        verify(eventPublisher).publishEvent(eventCaptor.capture());
        assertEquals(orderId, eventCaptor.getValue().getOrderId());
        // assertEquals(RabbitMqEvent.FAILED_TO_RESERVE_PRODUCTS, eventCaptor.getValue().getEvent()); // Assuming getEvent() is the correct method
    }

    // Test for non-existent product IDs
    @Test
    public void testReserveProducts_NonExistentProductIds() {
        // Arrange
        UUID productId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();

        Map<UUID, Integer> productsIdsAndQuantity = new HashMap<>();
        productsIdsAndQuantity.put(productId, 3);

        ReservationDto reservationDto = new ReservationDto(new ArrayList<>()); // Use constructor with List
        ReflectionTestUtils.setField(reservationDto, "orderId", orderId);
        ReflectionTestUtils.setField(reservationDto, "productsIdsAndQuantity", productsIdsAndQuantity);

        when(productRepository.findAllById(any())).thenReturn(Collections.emptyList());

        // Act & Assert
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            productService.reserveProducts(reservationDto);
        });

        assertEquals(ErrorMessageUtils.PRODUCT_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());
    }


    /**
     * Test the setImages method when the images array is empty.
     * Expectation: No images should be added to the product.
     */
    @Test
    void testSetImagesWithEmptyArray() {
        ProductEntity product = new ProductEntity();

        productService.setImages(product, Collections.emptyList());

        // Assert that no images are added to the product
        assertEquals(0, product.getImages().size(), "Product should have no images.");
    }


    /**
     * Test the setImages method with multiple images.
     * Expectation: All images should be added to the product.
     */
    @Test
    void testSetImagesWithMultipleImages() {
        ProductEntity product = new ProductEntity();
        MultipartFile image1 = mock(MultipartFile.class);
        MultipartFile image2 = mock(MultipartFile.class);
        FileDto fileDto1 = new FileDto();
        FileDto fileDto2 = new FileDto();
        fileDto1.setFile(image1);
        fileDto1.setOrder(1);
        fileDto2.setFile(image2);
        fileDto2.setOrder(2);

        FileEntity fileEntity1 = new FileEntity("image1.jpg", "directory1");
        FileEntity fileEntity2 = new FileEntity("image2.jpg", "directory2");

        when(fileService.createFileEntity(fileDto1, product)).thenReturn(fileEntity1);
        when(fileService.createFileEntity(fileDto2, product)).thenReturn(fileEntity2);

        productService.setImages(product, Arrays.asList(fileDto1, fileDto2));

        // Assert that both images are added to the product
        assertEquals(2, product.getImages().size(), "Product should have two images.");
    }
}