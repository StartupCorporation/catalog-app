package com.deye.web.unit.service;

import com.deye.web.controller.dto.CreateAttributeDto;
import com.deye.web.controller.dto.response.AttributeResponseDto;
import com.deye.web.entity.AttributeEntity;
import com.deye.web.exception.EntityNotFoundException;
import com.deye.web.repository.AttributeRepository;
import com.deye.web.service.AttributeService;
import com.deye.web.unit.service.factories.AttributeTestFactory;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import com.deye.web.util.mapper.AttributeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
public class AttributeServiceTest {

    @InjectMocks
    private AttributeService attributeService;

    @Mock
    private AttributeMapper attributeMapper;

    @Mock
    private AttributeRepository attributeRepository;

    private AttributeEntity attributeEntity;
    private CreateAttributeDto createAttributeDto;

    @BeforeEach
    public void setUp() {
        attributeEntity = AttributeTestFactory.createAttributeEntity();
        createAttributeDto = AttributeTestFactory.createCreateAttributeDto();
    }

    @Test
    public void shouldSaveAttribute() {
        attributeService.save(createAttributeDto);

        verify(attributeRepository).save(any());
    }

    @Test
    public void shouldGetAllAttributes() {
        when(attributeMapper.toAttributeView(any(AttributeEntity.class))).thenCallRealMethod();
        when(attributeRepository.findAll()).thenReturn(List.of(attributeEntity));
        List<AttributeResponseDto> attributes = attributeService.getAll();

        AttributeResponseDto attributeResponseDto = attributes.get(0);
        attributeAndDtoAssertions(attributeEntity, attributeResponseDto);
        verify(attributeRepository).findAll();
    }

    @Test
    public void shouldGetAttributeByIdWhenAttributeExistsInRepository() {
        UUID attributeId = UUID.randomUUID();
        when(attributeMapper.toAttributeView(any(AttributeEntity.class))).thenCallRealMethod();
        when(attributeRepository.findById(attributeId)).thenReturn(Optional.of(attributeEntity));

        AttributeResponseDto attributeResponseDto = attributeService.getById(attributeId);
        attributeAndDtoAssertions(attributeEntity, attributeResponseDto);

        verify(attributeRepository).findById(attributeId);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenAttributeNotFoundInRepositoryDuringSearchById() {
        UUID attributeId = UUID.randomUUID();
        when(attributeMapper.toAttributeView(any(AttributeEntity.class))).thenCallRealMethod();
        when(attributeRepository.findById(attributeId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> attributeService.getById(attributeId));

        assertEquals(ErrorCodeUtils.ATTRIBUTE_NOT_FOUND_ERROR_CODE, exception.getCode());
        assertEquals(ErrorMessageUtils.ATTRIBUTE_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());

        verify(attributeRepository).findById(attributeId);
    }

    @Test
    public void shouldDeleteAttributeWhenAttributeExistsInRepository() {
        UUID attributeId = UUID.randomUUID();
        when(attributeRepository.existsById(attributeId)).thenReturn(true);

        attributeService.deleteByID(attributeId);
        verify(attributeRepository).deleteById(attributeId);
    }

    @Test
    public void shouldThrowEntityNotFoundExceptionWhenAttributeNotFoundInRepositoryDuringDeletion() {
        UUID attributeId = UUID.randomUUID();
        when(attributeRepository.existsById(attributeId)).thenReturn(false);

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> attributeService.deleteByID(attributeId));

        assertEquals(ErrorCodeUtils.ATTRIBUTE_NOT_FOUND_ERROR_CODE, exception.getCode());
        assertEquals(ErrorMessageUtils.ATTRIBUTE_NOT_FOUND_ERROR_MESSAGE, exception.getMessage());

        verify(attributeRepository).existsById(attributeId);
    }

    private void attributeAndDtoAssertions(AttributeEntity attributeEntity, AttributeResponseDto attributeResponseDto) {
        assertEquals(attributeEntity.getId(), attributeResponseDto.getId());
        assertEquals(attributeEntity.getName(), attributeResponseDto.getName());
        assertEquals(attributeEntity.getDescription(), attributeResponseDto.getDescription());
        assertNull(attributeResponseDto.getValue());
    }
}
