package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.v1.resources.TemplateResource;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Template;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TemplateMapper {

    Template toEntity(TemplateResource resource);
    TemplateResource toResource(Template entity);

    List<TemplateResource> templatesToResources(List<Template> entities);

}
