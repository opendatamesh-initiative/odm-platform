package org.opendatamesh.odm.cli.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public final class ObjectMapperUtils {

    private static final ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static <T> List<T> extractListFromPageFromObjectNode(ObjectNode content, Class<T> targetClassType) {
        Page<?> page = objectMapper.convertValue(content, Page.class);
        return page.getContent().stream()
                .map(record -> objectMapper.convertValue(record, targetClassType))
                .collect(Collectors.toList());
    }

    public static <T> T convertString(String content, TypeReference<T> typeReference) {
        return objectMapper.convertValue(content, typeReference);
    }

    public static <T> T convertObject(Object content, Class<T> targetClassType) {
        return objectMapper.convertValue(content, targetClassType);
    }
    public static <T> T convertObjectNode(ObjectNode content, Class<T> targetClassType) {
        return objectMapper.convertValue(content, targetClassType);
    }

    public static <R> String formatAsString(R resource) throws JsonProcessingException {
        return objectMapper.writeValueAsString(resource);
    }

    public static <T> T stringToResource(String resourceString, Class<T> targetClassType) throws JsonProcessingException {
        return objectMapper.readValue(
                resourceString,
                targetClassType
        );
    }

}
