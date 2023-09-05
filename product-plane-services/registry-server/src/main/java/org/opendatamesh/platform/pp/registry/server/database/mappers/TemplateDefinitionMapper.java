package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.resources.DefinitionResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.Template;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TemplateDefinitionMapper { 
    
    Template toEntity(DefinitionResource resource);
    DefinitionResource toResource(Template entity);

    List<DefinitionResource> definitionsToResources(List<Template> entities);
}
