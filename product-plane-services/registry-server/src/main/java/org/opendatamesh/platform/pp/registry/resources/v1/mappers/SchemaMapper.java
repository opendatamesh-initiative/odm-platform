package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.api.v1.resources.ApiToSchemaRelationshipResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.SchemaResource;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ApiToSchemaRelationship;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Schema;

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
