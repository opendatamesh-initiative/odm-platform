package org.opendatamesh.platform.pp.registry.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;

public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final Logger logger = LoggerFactory.getLogger(HashMapConverter.class);
    @Autowired
    private ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    @Override
    public String convertToDatabaseColumn(Map<String, Object> map) {
        String jsonString = null;
        try {
            jsonString = objectMapper.writeValueAsString(map);
        } catch (final JsonProcessingException e) {
            logger.error("JSON writing error", e);
        }
        return jsonString;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String jsonString) {
        Map<String, Object> map = null;
        try {
            map = objectMapper.readValue(jsonString, Map.class);
        } catch (final IOException e) {
            logger.error("JSON reading error", e);
        }
        return map;
    }
}