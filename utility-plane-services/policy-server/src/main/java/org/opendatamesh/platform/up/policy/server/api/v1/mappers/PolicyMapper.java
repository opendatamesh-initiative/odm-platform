package org.opendatamesh.platform.up.policy.server.api.v1.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.up.policy.api.v1.resources.PolicyResource;
import org.opendatamesh.platform.up.policy.server.database.entities.PolicyEntity;

@Mapper(componentModel = "spring")
public interface PolicyMapper {

    PolicyResource toResource(PolicyEntity policy);

    PolicyEntity toEntity(PolicyResource policy);
    Iterable<PolicyEntity> toEntity(Iterable<PolicyResource> policies);
    Iterable<PolicyResource> toResource(Iterable<PolicyEntity> policies);
    
}