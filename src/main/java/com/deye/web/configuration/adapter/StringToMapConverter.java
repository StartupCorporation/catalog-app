package com.deye.web.configuration.adapter;

import com.deye.web.exception.JsonException;
import com.deye.web.util.error.ErrorCodeUtils;
import com.deye.web.util.error.ErrorMessageUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class StringToMapConverter implements Converter<String, Map<UUID, Object>> {
    private final ObjectMapper objectMapper;

    @Override
    public Map<UUID, Object> convert(String source) {
        try {
            return objectMapper.readValue(source, new TypeReference<>() {
            });
        } catch (Exception e) {
            log.error("JSON deserialization error: {}", source, e);
            throw new JsonException(ErrorCodeUtils.JSON_PARSE_ERROR_CODE, ErrorMessageUtils.JSON_PARSE_ERROR_MESSAGE);
        }
    }
}
