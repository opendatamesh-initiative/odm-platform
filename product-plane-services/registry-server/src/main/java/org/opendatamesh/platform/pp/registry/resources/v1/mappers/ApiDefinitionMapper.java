package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DefinitionResource;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ApiDefinition;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApiDefinitionMapper { 
    
    ApiDefinition toEntity(DefinitionResource resource);
    DefinitionResource toResource(ApiDefinition entity);

    List<DefinitionResource> definitionsToResources(List<ApiDefinition> entities);
}
