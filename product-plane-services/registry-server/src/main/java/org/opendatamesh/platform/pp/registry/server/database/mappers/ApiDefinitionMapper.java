package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.Api;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ApiDefinitionMapper { 
    
    Api toEntity(ExternalComponentResource resource);
    ExternalComponentResource toResource(Api entity);

    List<ExternalComponentResource> definitionsToResources(List<Api> entities);
}
