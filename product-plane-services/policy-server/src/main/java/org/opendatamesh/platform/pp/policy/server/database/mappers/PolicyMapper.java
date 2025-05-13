package org.opendatamesh.platform.pp.policy.server.database.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.opendatamesh.platform.core.commons.database.mappers.BaseMapper;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.springframework.util.StringUtils;

@Mapper(componentModel = "spring")
public interface PolicyMapper extends BaseMapper<PolicyResource, Policy> {

    @Override
    @Mapping(source = "externalContext", target = "externalContext", qualifiedByName = "objectNodeToString")
    Policy toEntity(PolicyResource resource);

    @Override
    @Mapping(source = "externalContext", target = "externalContext", qualifiedByName = "stringToObjectNode")
    PolicyResource toRes(Policy entity);

    @Named("stringToObjectNode")
    static ObjectNode stringToObjectNode(String json) {
        if (!StringUtils.hasText(json)) {
            return null;
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            return (ObjectNode) mapper.readTree(json);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert String to ObjectNode", e);
        }
    }

    @Named("objectNodeToString")
    static String objectNodeToString(ObjectNode json) {
        if (json == null) {
            return null;
        }
        try {
            return json.isTextual() ? json.asText() : json.toString();
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert String to ObjectNode", e);
        }
    }
}