package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.resources.DefinitionResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.Api;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApiDefinitionMapper { 
    
    Api toEntity(DefinitionResource resource);
    DefinitionResource toResource(Api entity);

    List<DefinitionResource> definitionsToResources(List<Api> entities);
}
