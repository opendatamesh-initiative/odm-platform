package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DefinitionResource;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Definition;

@Mapper(componentModel = "spring")
public interface DefinitionMapper { 
    
    Definition toEntity(DefinitionResource resource);
    DefinitionResource toResource(Definition entity);

    List<DefinitionResource> definitionsToResources(List<Definition> entities);
}
