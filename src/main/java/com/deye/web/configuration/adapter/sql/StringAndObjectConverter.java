package com.deye.web.configuration.adapter.sql;

import com.deye.web.exception.JsonException;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Converter
@Component
@Slf4j
@RequiredArgsConstructor
public class StringAndObjectConverter implements AttributeConverter<Object, String> {
    private final ObjectMapper objectMapper;

    @Override
    public String convertToDatabaseColumn(Object attribute) {
        try {
            return (attribute == null) ? null : objectMapper.writeValueAsString(attribute);
        } catch (JsonProcessingException e) {
            log.error("JSON serialization error: {}", attribute, e);
            throw new JsonException(ErrorCodeUtils.JSON_PARSE_ERROR_CODE, ErrorMessageUtils.JSON_PARSE_ERROR_MESSAGE);
        }
    }

    @Override
    public Object convertToEntityAttribute(String dbData) {
        try {
            if (dbData == null || dbData.trim().isEmpty()) {
                return Collections.emptyMap();
            }
            return objectMapper.readValue(dbData, new TypeReference<Object>() {
            });
        } catch (Exception e) {
            log.error("JSON deserialization error: {}", dbData, e);
            throw new JsonException(ErrorCodeUtils.JSON_PARSE_ERROR_CODE, ErrorMessageUtils.JSON_PARSE_ERROR_MESSAGE);
        }
    }
}
