package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.*;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ExternalResourceMapper {
    ExternalResource toEntity(ExternalResourceResource resource);
    ExternalResourceResource toResource(ExternalResource entity);
}