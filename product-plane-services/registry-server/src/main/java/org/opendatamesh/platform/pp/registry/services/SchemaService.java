package org.opendatamesh.platform.pp.registry.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ApiToSchemaRelationship;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Schema;
import org.opendatamesh.platform.pp.registry.database.repositories.ApiToSchemaRelationshipRepository;
import org.opendatamesh.platform.pp.registry.database.repositories.SchemaRepository;
import org.opendatamesh.platform.pp.registry.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.registry.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.exceptions.UnprocessableEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class SchemaService {

    @Autowired
    private SchemaRepository schemaRepository;

    @Autowired
    private ApiToSchemaRelationshipRepository relationshipRepository;

    private static final Logger logger = LoggerFactory.getLogger(SchemaService.class);

    public SchemaService() {

    }

    // ======================================================================================
    // CREATE
    // ======================================================================================

    public Schema createSchema(Schema schema) {

        if (schema == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Schema object cannot be null");
        }

        if (!StringUtils.hasText(schema.getContent())) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_08_DEFINITION_DOC_SYNTAX_IS_INVALID,
                    "Schema content property cannot be empty");
        }

        if (!StringUtils.hasText(schema.getName())) {
            String fqn = UUID.nameUUIDFromBytes(schema.getContent().getBytes()).toString();
            schema.setName(fqn);
            logger.warn("Schema has no name. An UUID type-3 generated from its content will be used as name");
        }

        if (!StringUtils.hasText(schema.getVersion())) {
            schema.setVersion("1.0.0");
            logger.warn("Schema has no version. Version 1.0.0 will be used as default");
        }

        if (schemaExists(schema.getName(), schema.getVersion())) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_11_SCHEMA_ALREADY_EXISTS,
                    "Schema [" + schema.getName() + "(v. " + schema.getVersion() + ")] already exists");
        }

        try {
            schema = saveSchema(schema);
            logger.info("Schema [" + schema.getId() + "] successfully created");
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving schema",
                    t);
        }

        return schema;
    }

    private Schema saveSchema(Schema schema) {
        return schemaRepository.saveAndFlush(schema);
    }

    // -------------------------
    // create relationship
    // -------------------------

     public ApiToSchemaRelationship createApiToSchemaRelationship(ApiToSchemaRelationship relationship) {

        if (relationship == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Relationship object cannot be null");
        }

        if (relationship.getId() == null) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_08_DEFINITION_DOC_SYNTAX_IS_INVALID,
                    "Relationship id property cannot be empty");
        }

        if (relationship.getId().getApiId() == null) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_08_DEFINITION_DOC_SYNTAX_IS_INVALID,
                    "Relationship apiId property cannot be empty");
        }

        if (relationship.getId().getSchemaId() == null) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_08_DEFINITION_DOC_SYNTAX_IS_INVALID,
                    "Relationship schemaId property cannot be empty");
        }

      

        if (relationshipExists(relationship.getId().getApiId(), relationship.getId().getSchemaId())) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_12_SCHEMA_TO_API_REL_ALREADY_EXISTS,
                    "Schema [" + relationship.getId().getSchemaId()+ " relationship with api " + relationship.getId().getApiId() + "] already exists");
        }

        try {
            relationship = saveRelationship(relationship);
            logger.info("Schema [" + relationship.getId().getSchemaId()+ " relationship with api [" + relationship.getId().getApiId() + "] successfully created");
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving relationship",
                    t);
        }

        return relationship;
    }

    private ApiToSchemaRelationship saveRelationship(ApiToSchemaRelationship relationship) {
        return relationshipRepository.saveAndFlush(relationship);
    }

    // ======================================================================================
    // READ
    // ======================================================================================

    public Schema readOneSchema(Schema schema) {
        if (schema == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Schema object cannot be null");
        }

        return readSchema(schema.getId());
    }

    public Schema readSchema(Long schemaId) {
        Schema schema = null;

        schema = searchSchema(schemaId);

        if (schema == null) {
            throw new NotFoundException(
                    OpenDataMeshAPIStandardError.SC404_04_SCHEMA_NOT_FOUND,
                    "Schema [" + schemaId + "] does not exist");
        }

        return schema;
    }

    public Schema loadSchema(Long schemaId) {
        Schema schema = null;
        Optional<Schema> referenceObjectLookUpResults = schemaRepository.findById(schemaId);

        if (referenceObjectLookUpResults.isPresent()) {
            schema = referenceObjectLookUpResults.get();
        }
        return schema;
    }

    // -------------------------
    // exists methods
    // -------------------------

    private boolean schemaExists(String name, String version) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(version)) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "name and version property cannot be null");
        }
        return schemaRepository.existsByNameAndVersion(name, version);
    }

    private boolean schemaExists(Long schemaId) {
        return schemaId != null
                && schemaRepository.existsById(schemaId);
    }

     private boolean relationshipExists(Long apiId, Long schemaId) {
        if (apiId == null ||  schemaId == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "apiId and schemaId property cannot be null");
        }
        return relationshipRepository.existsByIdApiIdAndIdSchemaId(apiId, schemaId);
    }

    // -------------------------
    // search methods
    // -------------------------

    public Schema searchSchema(Long schemaId) {
        Schema schema = null;
        if (schemaId == null) {
            throw new BadRequestException(
                    OpenDataMeshAPIStandardError.SC400_13_SCHEMA_ID_IS_EMPTY,
                    "Schema id cannot be empty");
        }

        try {
            schema = loadSchema(schemaId);
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading schema [" + schemaId + "]",
                    t);
        }

        return schema;
    }

    /**
     * @return The schema identified by name and version. Null if not exists
     */
    public Schema searchSchema(
            String name,
            String version) {

        Schema schema = null;
        List<Schema> schemas = searchSchemas(null, name, version);
        if (schemas == null || schemas.size() == 0) {
            schema = null;
        } else if (schemas.size() == 1) {
            schema = schemas.get(0);
        } else {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching schema");
        }

        return schema;
    }

    public List<Schema> searchSchemas(
            Long apiId,
            String name,
            String version) {
        List<Schema> schemaSearchResults = null;
        try {
            schemaSearchResults = findSchemas(apiId, name, version);
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching schema",
                    t);
        }
        return schemaSearchResults;
    }

    private List<Schema> findSchemas(
            Long apiId,
            String name,
            String version) {

        List<Schema> schemas = new ArrayList<Schema>();

        if(apiId != null) {
            List<ApiToSchemaRelationship> relationships = relationshipRepository.findByIdApiId(apiId);
            for(ApiToSchemaRelationship relationship: relationships) {
                Schema schema = loadSchema(relationship.getId().getSchemaId());
                if(schema != null) {
                    boolean nameMatch = name == null || (name!=null && schema.getName().equals(name));
                    boolean versionMatch = version == null || (version!=null && schema.getVersion().equals(version));
                    if(versionMatch && nameMatch) {
                        schemas.add(schema); 
                    }
                } else {
                    logger.warn("The schema [" + relationship.getId().getSchemaId() + "] associated to api [" + apiId + "] does not exist anymore");
                }
            }
        } else {
             schemas = schemaRepository
                .findAll(SchemaRepository.Specs.hasMatch(name, version));
        }
       
        
        return schemas;
    }

    public List<ApiToSchemaRelationship> readSchemaRealtionships(Long schemaId) {
        if(!schemaExists(schemaId)) {
            // eccezione
        }
        List<ApiToSchemaRelationship> relationships = relationshipRepository.findByIdSchemaId(schemaId);
        return relationships;
    }

    // ======================================================================================
    // UPDATE
    // ======================================================================================

    public Schema updateSchema(Schema schema) {
        if (schema == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Schema object cannot be null");
        }

        if (!schemaExists(schema.getId())) {
            throw new NotFoundException(
                    OpenDataMeshAPIStandardError.SC404_04_SCHEMA_NOT_FOUND,
                    "Schema [" + schema.getId() + "] does not exist");
        }

        try {
            schema = saveSchema(schema);
            logger.info("Schema [" + schema.getId() + "] successfully updated");
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while updating schema [" + schema.getId() + "]",
                    t);
        }

        return schema;
    }

    // ======================================================================================
    // DELETE
    // ======================================================================================

    public void deleteSchema(Long schemaId) {
        Schema schema = searchSchema(schemaId);
        if (schema == null) {
            throw new NotFoundException(
                    OpenDataMeshAPIStandardError.SC404_04_SCHEMA_NOT_FOUND,
                    "Schema [" + schemaId + "] does not exist");
        }

        try {
            schemaRepository.delete(schema);
            logger.info("Schema [" + schemaId + "] successfully deleted");
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while deleting schema [" + schemaId + "]",
                    t);
        }

    }
}
