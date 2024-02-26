package org.opendatamesh.platform.pp.policy.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.mappers.utils.BaseMapper;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyRes;

@Mapper(componentModel = "spring")
public interface PolicyMapper extends BaseMapper<PolicyRes, Policy> {
}
