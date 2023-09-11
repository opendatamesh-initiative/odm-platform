package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.resources.DomainResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct.Domain;

import java.util.List;


@Mapper(componentModel = "spring")
public interface DomainMapper {

    Domain toEntity(DomainResource resource);
    DomainResource toResource(Domain entity);

    List<DomainResource> domainToResources(List<Domain> entities);
}
