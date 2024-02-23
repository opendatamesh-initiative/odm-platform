package org.opendatamesh.platform.pp.policy.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.mappers.utils.BaseMapper;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;

@Mapper(componentModel = "spring")
public interface PolicyEngineMapper extends BaseMapper<PolicyEngineResource, PolicyEngine> {
}
