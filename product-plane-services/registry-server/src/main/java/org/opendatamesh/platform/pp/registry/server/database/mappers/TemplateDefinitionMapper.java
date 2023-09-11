package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.Template;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TemplateDefinitionMapper { 
    
    Template toEntity(ExternalComponentResource resource);
    ExternalComponentResource toResource(Template entity);

    List<ExternalComponentResource> definitionsToResources(List<Template> entities);
}
