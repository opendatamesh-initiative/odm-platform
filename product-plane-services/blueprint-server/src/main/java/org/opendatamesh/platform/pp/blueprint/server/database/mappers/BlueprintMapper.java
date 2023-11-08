package org.opendatamesh.platform.pp.blueprint.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.opendatamesh.platform.pp.blueprint.server.database.entities.Blueprint;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BlueprintMapper {

    BlueprintResource toResource(Blueprint blueprints);
    Blueprint toEntity(BlueprintResource blueprints);
    List<Blueprint> toEntities(List<BlueprintResource> blueprints);
    List<BlueprintResource> toResources(List<Blueprint> blueprints);
    
}
