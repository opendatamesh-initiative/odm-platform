package org.opendatamesh.platform.pp.policy.server.database.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.opendatamesh.platform.core.commons.database.mappers.BaseMapper;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultShortResource;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResultShort;

@Mapper(componentModel = "spring", uses = {PolicyMapper.class})
public interface PolicyEvaluationResultShortMapper extends BaseMapper<PolicyEvaluationResultShortResource, PolicyEvaluationResultShort> {

    @Override
    @Mapping(target = "policy", source = "policy")
    PolicyEvaluationResultShortResource toRes(PolicyEvaluationResultShort entity);

    // Note: We don't need toEntity method for short resource as it's only used for reading
} 