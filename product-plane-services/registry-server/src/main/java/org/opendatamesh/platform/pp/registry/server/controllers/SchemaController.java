package org.opendatamesh.platform.pp.registry.server.controllers;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.api.controllers.AbstractSchemaController;
import org.opendatamesh.platform.pp.registry.api.resources.ApiToSchemaRelationshipResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.api.resources.SchemaResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.ApiToSchemaRelationship;
import org.opendatamesh.platform.pp.registry.server.database.mappers.SchemaMapper;
import org.opendatamesh.platform.pp.registry.server.services.SchemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SchemaController extends AbstractSchemaController {

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private SchemaMapper schemaMapper;

    private static final Logger logger = LoggerFactory.getLogger(SchemaController.class);

    public SchemaController() {
        logger.debug("Schemas controller successfully started");
    }

    @Override
    public SchemaResource createSchema(
            SchemaResource schemaRes) {
        if (schemaRes == null) {
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_12_SCHEMA_IS_EMPTY,
                    "Schema cannot be empty");
        }

        org.opendatamesh.platform.pp.registry.server.database.entities.Schema schema = schemaMapper.toEntity(schemaRes);
        schema = schemaService.createSchema(schema);
        return schemaMapper.toResource(schema);
    }

    @Override
    public List<SchemaResource> getSchemas(
            String name,
            String version,
            String apiId,
            boolean includeContent) {
        
        List<org.opendatamesh.platform.pp.registry.server.database.entities.Schema> schemas;
        schemas = schemaService.searchSchemas(apiId, name, version);
        List<SchemaResource> schemaResources = schemaMapper.schemasToResources(schemas);
        if (includeContent == false) {
            for (SchemaResource schemaResource : schemaResources) {
                schemaResource.setContent(null);
            }
        }
        return schemaResources;
    }

    @Override
    public SchemaResource getSchema(Long id) {
        org.opendatamesh.platform.pp.registry.server.database.entities.Schema schema;
        schema = schemaService.readSchema(id);
        SchemaResource schemaResource = schemaMapper.toResource(schema);
        return schemaResource;
    }

    @Override
    public String getSchemaContent(Long id) {
        org.opendatamesh.platform.pp.registry.server.database.entities.Schema schema;
        schema = schemaService.readSchema(id);
        return schema.getContent();
    }

    @Override
    public List<ApiToSchemaRelationshipResource> getSchemaRelationships(Long id) {
        List<ApiToSchemaRelationship> relationships = schemaService.readSchemaRealtionships(id); // just to check that                                                                       // the schema exists
        return schemaMapper.relationshipsToResources(relationships);
    }

    @Override
    public void deleteSchema(Long id) {
        schemaService.deleteSchema(id);
    }

}
