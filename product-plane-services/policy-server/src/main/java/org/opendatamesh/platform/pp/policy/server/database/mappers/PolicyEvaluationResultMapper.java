package org.opendatamesh.platform.pp.policy.server.database.mappers;

import com.fasterxml.jackson.databind.JsonNode;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.opendatamesh.platform.pp.policy.api.mappers.utils.JsonNodeUtils;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;
import org.opendatamesh.platform.pp.policy.server.database.mappers.utils.BaseMapper;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;

@Mapper(componentModel = "spring")
public interface PolicyEvaluationResultMapper extends BaseMapper<PolicyEvaluationResultResource, PolicyEvaluationResult> {

    @Override
    @Mapping(target = "inputObject", source = "entity.inputObject", qualifiedByName = "stringToJsonNode")
    PolicyEvaluationResultResource toRes(PolicyEvaluationResult entity);

    @Override
    @Mapping(target = "inputObject", source = "resource.inputObject", qualifiedByName = "jsonNodeToString")
    PolicyEvaluationResult toEntity(PolicyEvaluationResultResource resource);


    @Named("stringToJsonNode")
    static JsonNode stringToJsonNode(String value) {
        return JsonNodeUtils.toJsonNode(value);
    };

    @Named("jsonNodeToString")
    static String jsonNodeToString(JsonNode value) {
        return JsonNodeUtils.toStringFromJsonNode(value);
    };

}