package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.opendatamesh.platform.pp.policy.api.mappers.EventTypeBaseMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventTypeMapper extends EventTypeBaseMapper {

}
