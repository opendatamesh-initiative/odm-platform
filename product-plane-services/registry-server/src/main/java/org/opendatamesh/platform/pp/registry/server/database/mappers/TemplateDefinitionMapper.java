package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.resources.DefinitionResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.sharedres.TemplateDefinition;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TemplateDefinitionMapper { 
    
    TemplateDefinition toEntity(DefinitionResource resource);
    DefinitionResource toResource(TemplateDefinition entity);

    List<DefinitionResource> definitionsToResources(List<TemplateDefinition> entities);
}
