package org.opendatamesh.platform.pp.policy.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicySuite;
import org.opendatamesh.platform.pp.policy.server.database.mappers.utils.BaseMapper;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySuiteRes;

@Mapper(componentModel = "spring")
public interface PolicySuiteMapper extends BaseMapper<PolicySuiteRes, PolicySuite> {
}
