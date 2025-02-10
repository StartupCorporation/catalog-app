package com.deye.web.configuration.adapter;

import com.deye.web.exception.JsonException;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import com.google.gson.Gson;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Converter(autoApply = true)
@Component
@RequiredArgsConstructor
@Slf4j
public class SQLJsonConverter implements AttributeConverter<Map<String, Object>, String> {
    private final Gson gson;

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return attribute == null ? null : gson.toJson(attribute);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JsonException(ErrorCodeUtils.JSON_PARSE_ERROR_CODE, ErrorMessageUtils.JSON_PARSE_ERROR_MESSAGE);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? null : gson.fromJson(dbData, Map.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new JsonException(ErrorCodeUtils.JSON_PARSE_ERROR_CODE, ErrorMessageUtils.JSON_PARSE_ERROR_MESSAGE);
        }
    }
}
