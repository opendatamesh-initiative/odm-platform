package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DefinitionResource;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.TemplateDefinition;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TemplateDefinitionMapper { 
    
    TemplateDefinition toEntity(DefinitionResource resource);
    DefinitionResource toResource(TemplateDefinition entity);

    List<DefinitionResource> definitionsToResources(List<TemplateDefinition> entities);
}
