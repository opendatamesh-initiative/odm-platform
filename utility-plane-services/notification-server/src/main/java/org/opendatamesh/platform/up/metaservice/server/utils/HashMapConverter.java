package org.opendatamesh.platform.up.metaservice.server.utils;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.AttributeConverter;
import java.io.IOException;
import java.util.Map;


public class HashMapConverter implements AttributeConverter<Map<String, Object>, String> {

    private static final Logger logger = LoggerFactory.getLogger(HashMapConverter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> map) {

        String string = null;

        try {
            string = objectMapper.writeValueAsString(map);
        } catch (final JsonProcessingException e) {
            logger.error("JSON writing error", e);
        }

        return string;
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String string) {

        Map<String, Object> map = null;
        try {
            map = objectMapper.readValue(string, Map.class);
        } catch (final IOException e) {
            logger.error("JSON reading error", e);
        }

        return map;
    }

}
