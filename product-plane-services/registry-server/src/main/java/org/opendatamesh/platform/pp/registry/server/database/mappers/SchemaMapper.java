package org.opendatamesh.platform.pp.registry.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.resources.ApiToSchemaRelationshipResource;
import org.opendatamesh.platform.pp.registry.api.resources.SchemaResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.sharedres.ApiToSchemaRelationship;
import org.opendatamesh.platform.pp.registry.server.database.entities.sharedres.Schema;

import java.util.List;


@Mapper(componentModel = "spring")
public interface SchemaMapper { 
    
    Schema toEntity(SchemaResource resource);
    SchemaResource toResource(Schema entity);

    List<SchemaResource> schemasToResources(List<Schema> entities);

    ApiToSchemaRelationship toEntity(ApiToSchemaRelationshipResource resource);
    ApiToSchemaRelationshipResource toResource(ApiToSchemaRelationship entity);

    List<ApiToSchemaRelationshipResource> relationshipsToResources(List<ApiToSchemaRelationship> entities);
}
