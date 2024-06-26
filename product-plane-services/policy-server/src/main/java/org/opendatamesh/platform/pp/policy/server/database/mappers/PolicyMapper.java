package org.opendatamesh.platform.pp.policy.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.core.commons.database.mappers.BaseMapper;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;

@Mapper(componentModel = "spring")
public interface PolicyMapper extends BaseMapper<PolicyResource, Policy> {
}
