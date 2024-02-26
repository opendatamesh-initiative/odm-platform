package org.opendatamesh.platform.pp.policy.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;
import org.opendatamesh.platform.pp.policy.server.database.mappers.utils.BaseMapper;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;

@Mapper(componentModel = "spring")
public interface PolicyEvaluationResultMapper extends BaseMapper<PolicyEvaluationResultResource, PolicyEvaluationResult> {
}
