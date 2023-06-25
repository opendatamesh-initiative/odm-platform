package org.opendatamesh.platform.pp.registry.resources.v1.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ApiToSchemaRelationship;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Schema;
import org.opendatamesh.platform.pp.registry.resources.v1.ApiToSchemaRelationshipResource;
import org.opendatamesh.platform.pp.registry.resources.v1.SchemaResource;

@Mapper(componentModel = "spring")
public interface SchemaMapper { 
    
    Schema toEntity(SchemaResource resource);
    SchemaResource toResource(Schema entity);

    List<SchemaResource> schemasToResources(List<Schema> entities);

    ApiToSchemaRelationship toEntity(ApiToSchemaRelationshipResource resource);
    ApiToSchemaRelationshipResource toResource(ApiToSchemaRelationship entity);

    List<ApiToSchemaRelationshipResource> relationshipsToResources(List<ApiToSchemaRelationship> entities);
}
