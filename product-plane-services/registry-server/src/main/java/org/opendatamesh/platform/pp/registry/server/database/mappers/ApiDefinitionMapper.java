package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.resources.DefinitionResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.sharedres.ApiDefinition;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApiDefinitionMapper { 
    
    ApiDefinition toEntity(DefinitionResource resource);
    DefinitionResource toResource(ApiDefinition entity);

    List<DefinitionResource> definitionsToResources(List<ApiDefinition> entities);
}
