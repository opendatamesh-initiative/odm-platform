package org.opendatamesh.odm.cli.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.stream.Collectors;

public final class JsonUtils {

    private static final ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;

    public static final <T> List<T> extractListFromPageFromObjectNode(ObjectNode content, Class<T> targetClassType) {
        Page<?> page = objectMapper.convertValue(content, Page.class);
        return page.getContent().stream()
                .map(record -> objectMapper.convertValue(record, targetClassType))
                .collect(Collectors.toList());
    }

}
