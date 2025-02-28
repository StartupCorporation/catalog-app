package com.deye.web.configuration.adapter.sql;

import com.deye.web.entity.attribute.definition.AttributeDefinition;
import com.deye.web.exception.JsonException;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Converter
@Component
@Slf4j
@RequiredArgsConstructor
public class AttributeDefinitionAndStringConverter implements AttributeConverter<AttributeDefinition, String> {
    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(AttributeDefinition attribute) {
        try {
            return attribute == null ? null : objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage(), e);
            throw new JsonException(ErrorCodeUtils.JSON_PARSE_ERROR_CODE, ErrorMessageUtils.JSON_PARSE_ERROR_MESSAGE);
        }
    }

    @Override
    public AttributeDefinition convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : objectMapper.readValue(dbData, AttributeDefinition.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JsonException(ErrorCodeUtils.JSON_PARSE_ERROR_CODE, ErrorMessageUtils.JSON_PARSE_ERROR_MESSAGE);
        }
    }
}
