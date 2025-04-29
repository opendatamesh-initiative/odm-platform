package org.opendatamesh.platform.pp.policy.server.database.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.opendatamesh.platform.core.commons.database.mappers.BaseMapper;
import org.opendatamesh.platform.pp.policy.api.mappers.utils.JsonNodeUtils;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;
import org.springframework.util.StringUtils;

@Mapper(componentModel = "spring")
public interface PolicyEvaluationResultMapper extends BaseMapper<PolicyEvaluationResultResource, PolicyEvaluationResult> {

    @Override
    @Mapping(target = "inputObject", source = "inputObject", qualifiedByName = "stringToJsonNode")
    @Mapping(target = "policy.externalContext", source = "policy.externalContext", qualifiedByName = "stringToObjectNode")
    PolicyEvaluationResultResource toRes(PolicyEvaluationResult entity);

    @Override
    @Mapping(target = "inputObject", source = "inputObject", qualifiedByName = "jsonNodeToString")
    @Mapping(target = "policy.externalContext", source = "policy.externalContext", qualifiedByName = "objectNodeToString")
    PolicyEvaluationResult toEntity(PolicyEvaluationResultResource resource);


    @Named("stringToJsonNode")
    static JsonNode stringToJsonNode(String value) {
        return JsonNodeUtils.toJsonNode(value);
    }

    @Named("jsonNodeToString")
    static String jsonNodeToString(JsonNode value) {
        return JsonNodeUtils.toStringFromJsonNode(value);
    }

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