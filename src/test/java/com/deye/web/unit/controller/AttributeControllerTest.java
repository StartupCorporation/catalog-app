package com.deye.web.unit.controller;

import com.deye.web.controller.AttributeController;
import com.deye.web.controller.dto.CreateAttributeDto;
import com.deye.web.controller.dto.response.AttributeResponseDto;
import com.deye.web.service.AttributeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AttributeControllerTest {

    @Mock
    private AttributeService attributeService;

    @InjectMocks
    private AttributeController attributeController;

    /**
     * Test to ensure that the deleteAttributeById method
     * calls the deleteByID method of AttributeService with the correct UUID.
     */
    @Test
    void testDeleteAttributeById() {
        // Arrange
        UUID attributeId = UUID.randomUUID();

        // Act
        attributeController.deleteAttributeById(attributeId);

        // Assert
        verify(attributeService, times(1)).deleteByID(attributeId);
    }


    /**
     * Test to verify that the saveAttribute method calls the save method of AttributeService
     * with the correct CreateAttributeDto object.
     */
    @Test
    void testSaveAttribute() {
        // Arrange: Create a valid CreateAttributeDto object
        CreateAttributeDto createAttributeDto = new CreateAttributeDto();
        ReflectionTestUtils.setField(createAttributeDto, "name", "Test Attribute");
        ReflectionTestUtils.setField(createAttributeDto, "description", "Test Description");

        // Assuming AttributeDefinition is a valid class and has a default constructor
        // AttributeDefinition definition = new AttributeDefinition();
        // ReflectionTestUtils.setField(createAttributeDto, "definition", definition);

        // Act: Call the saveAttribute method
        attributeController.saveAttribute(createAttributeDto);

        // Assert: Verify that the save method of attributeService was called once with the correct argument
        verify(attributeService, times(1)).save(createAttributeDto);
    }

    // Test to verify that getAttributes returns the expected list of AttributeResponseDto
    @Test
    void testGetAttributesReturnsExpectedList() {
        // Arrange: Create a mock list of AttributeResponseDto using no-arg constructor
        AttributeResponseDto attribute1 = new AttributeResponseDto();
        ReflectionTestUtils.setField(attribute1, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(attribute1, "name", "Attribute1");

        AttributeResponseDto attribute2 = new AttributeResponseDto();
        ReflectionTestUtils.setField(attribute2, "id", UUID.randomUUID());
        ReflectionTestUtils.setField(attribute2, "name", "Attribute2");

        List<AttributeResponseDto> expectedAttributes = Arrays.asList(attribute1, attribute2);

        // Mock the behavior of attributeService.getAll() to return the mock list
        when(attributeService.getAll()).thenReturn(expectedAttributes);

        // Act: Call the getAttributes method
        List<AttributeResponseDto> actualAttributes = attributeController.getAttributes();

        // Assert: Verify that the returned list matches the expected list
        assertEquals(expectedAttributes, actualAttributes, "The returned attributes should match the expected attributes.");
    }


    /**
     * Test the successful retrieval of an attribute by ID.
     */
    @Test
    void testGetAttributeById_Success() {
        // Arrange
        UUID attributeId = UUID.randomUUID();
        AttributeResponseDto expectedResponse = new AttributeResponseDto();
        expectedResponse.setId(attributeId);
        expectedResponse.setName("Test Attribute");

        when(attributeService.getById(attributeId)).thenReturn(expectedResponse);

        // Act
        AttributeResponseDto actualResponse = attributeController.getAttributeById(attributeId);

        // Assert
        assertEquals(expectedResponse, actualResponse, "The response should match the expected attribute response.");
        verify(attributeService, times(1)).getById(attributeId);
    }


    /**
     * Test the behavior when the attribute service returns null.
     */
    @Test
    void testGetAttributeById_NotFound() {
        // Arrange
        UUID attributeId = UUID.randomUUID();
        when(attributeService.getById(attributeId)).thenReturn(null);

        // Act
        AttributeResponseDto actualResponse = attributeController.getAttributeById(attributeId);

        // Assert
        assertEquals(null, actualResponse, "The response should be null when the attribute is not found.");
        verify(attributeService, times(1)).getById(attributeId);
    }


    /**
     * Test the behavior when an exception is thrown by the attribute service.
     */
    @Test
    void testGetAttributeById_Exception() {
        // Arrange
        UUID attributeId = UUID.randomUUID();
        when(attributeService.getById(attributeId)).thenThrow(new RuntimeException("Service exception"));

        // Act & Assert
        try {
            attributeController.getAttributeById(attributeId);
        } catch (RuntimeException e) {
            assertEquals("Service exception", e.getMessage(), "The exception message should match the expected message.");
        }

        verify(attributeService, times(1)).getById(attributeId);
    }
}