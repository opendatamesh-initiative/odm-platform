package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.StandardDefinition;

@Mapper(componentModel = "spring")
public interface StandardDefinitionMapper { 
    
    StandardDefinition toEntity(StandardDefinitionDPDS resource);
    StandardDefinitionDPDS toResource(StandardDefinition entity);
}
