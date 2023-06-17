package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import org.mapstruct.Mapper;

import org.opendatamesh.platform.core.dpds.model.ExternalResourceDPDS;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.ExternalResource;


@Mapper(componentModel = "spring")
public interface ExternalResourceMapper {
    ExternalResource toEntity(ExternalResourceDPDS resource);
    ExternalResourceDPDS toResource(ExternalResource entity);
}
