package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Template;
import org.opendatamesh.platform.pp.registry.resources.v1.TemplateResource;

import java.util.List;

@Mapper(componentModel = "spring")
public interface TemplateMapper {

    Template toEntity(TemplateResource resource);
    TemplateResource toResource(Template entity);

    List<TemplateResource> templatesToResources(List<Template> entities);

}
