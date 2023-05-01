package org.opendatamesh.platform.pp.api.resources.v1.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.api.database.entities.sharedres.Definition;
import org.opendatamesh.platform.pp.api.resources.v1.sharedres.DefinitionResource;

@Mapper(componentModel = "spring")
public interface DefinitionMapper { 
    
    Definition toEntity(DefinitionResource resource);
    DefinitionResource toResource(Definition entity);

    List<DefinitionResource> definitionsToResources(List<Definition> entities);
}
