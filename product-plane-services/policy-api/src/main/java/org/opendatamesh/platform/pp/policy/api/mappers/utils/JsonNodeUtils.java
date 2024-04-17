package org.opendatamesh.platform.pp.policy.api.mappers.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

public final class JsonNodeUtils {

    private static final ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
    //private static final ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);

    public static JsonNode toJsonNode(Object object) {
        try {
            return mapper.readTree(mapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error serializing object as JSON",
                    e
            );
        }
    }

    public static JsonNode toJsonNode(String inputString) {
        try {
            return mapper.readTree(inputString);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error serializing object as JSON",
                    e
            );
        }
    }

    public static String toStringFromJsonNode(JsonNode jsonNode) {
        try {
            return mapper.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error deserializing JSON as string",
                    e
            );
        }
    }

}
