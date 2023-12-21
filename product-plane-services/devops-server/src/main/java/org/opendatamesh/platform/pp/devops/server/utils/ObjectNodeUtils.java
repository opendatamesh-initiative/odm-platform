package org.opendatamesh.platform.pp.devops.server.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

public final class ObjectNodeUtils {

    public static ObjectNode toObjectNode(Object object) throws JsonProcessingException {
        return readObjectNode(ObjectMapperFactory.JSON_MAPPER.writeValueAsString(object));
    }

    public static ObjectNode toObjectNode(String stringObject) throws JsonProcessingException {
        return readObjectNode(stringObject);
    }

    private static ObjectNode readObjectNode(String objectNodeContent) throws JsonProcessingException {
        return ObjectMapperFactory.JSON_MAPPER.readValue(
                objectNodeContent,
                ObjectNode.class
        );
    }

}
