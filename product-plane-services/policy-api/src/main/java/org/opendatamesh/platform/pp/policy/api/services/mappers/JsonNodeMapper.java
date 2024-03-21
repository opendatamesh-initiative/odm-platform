package org.opendatamesh.platform.pp.policy.api.services.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

public class JsonNodeMapper {

    private static final ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;

    public static final JsonNode toJsonNode(Object object) {
        try {
            return mapper.readTree(mapper.writeValueAsString(object));
        } catch (JsonProcessingException e) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Error serializing object as JSON",
                    e
            );
        }
    };

}
