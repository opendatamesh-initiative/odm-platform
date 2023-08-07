package org.opendatamesh.platform.pp.registry.services;

import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ApiDefinition;
import org.opendatamesh.platform.pp.registry.database.repositories.ApiDefinitionRepository;
import org.opendatamesh.platform.pp.registry.api.v1.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApiDefinitionService {

    @Autowired
    private ApiDefinitionRepository apiDefinitionRepository;

    private static final Logger logger = LoggerFactory.getLogger(ApiDefinitionService.class);

    public ApiDefinitionService() {

    }

    public ApiDefinition resolveNameAndVersion(ApiDefinition definition) {
         if (definition == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Definition object cannot be null");
        }

        if (!StringUtils.hasText(definition.getContent())) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_08_DEFINITION_DOC_SYNTAX_IS_INVALID,
                    "Definition content property cannot be empty");
        }

        if (!StringUtils.hasText(definition.getName())) {
            String fqn = UUID.nameUUIDFromBytes(definition.getContent().getBytes()).toString();
            definition.setName(fqn);
            logger.warn("Definition has no name. An UUID type-3 generated from its content will be used as name");
        }

        if (!StringUtils.hasText(definition.getVersion())) {
            definition.setVersion("1.0.0");
            logger.warn("Definition has no version. Version 1.0.0 will be used as default");
        }

        return definition;
    }

    // ======================================================================================
    // CREATE
    // ======================================================================================

    public ApiDefinition createDefinition(ApiDefinition definition) {

        if (definition == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Standard definition object cannot be null");
        }

        if (!StringUtils.hasText(definition.getContent())) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_08_DEFINITION_DOC_SYNTAX_IS_INVALID,
                    "Definition content property cannot be empty");
        }

       definition = resolveNameAndVersion(definition);

        if (definitionExists(definition.getName(), definition.getVersion())) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_06_STDDEF_ALREADY_EXISTS,
                    "Definition [" + definition.getName() + "(v. " + definition.getVersion() + ")] already exists");
        }

        try {
            definition = saveDefinition(definition);
            logger.info("Standard definition [" + definition.getId() + "] successfully created");
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving standard definition",
                    t);
        }

        return definition;
    }

    private ApiDefinition saveDefinition(ApiDefinition definition) {
        return apiDefinitionRepository.saveAndFlush(definition);
    }

    // ======================================================================================
    // READ
    // ======================================================================================

    public ApiDefinition readOneDefinition(ApiDefinition definition) {
        if (definition == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Definition object cannot be null");
        }

        return readDefinition(definition.getId());
    }

    public ApiDefinition readDefinition(Long definitionId) {
        ApiDefinition definition = null;

        definition = searchDefinition(definitionId);

        if (definition == null) {
            throw new NotFoundException(
                    OpenDataMeshAPIStandardError.SC404_03_STDDEF_NOT_FOUND,
                    "Definition [" + definitionId + "] does not exist");
        }

        return definition;
    }

    public ApiDefinition loadDefinition(Long definitionId) {
        ApiDefinition definition = null;
        Optional<ApiDefinition> referenceObjectLookUpResults = apiDefinitionRepository.findById(definitionId);

        if (referenceObjectLookUpResults.isPresent()) {
            definition = referenceObjectLookUpResults.get();
        }
        return definition;
    }

    // -------------------------
    // exists methods
    // -------------------------

    private boolean definitionExists(String name, String version) {
        if (!StringUtils.hasText(name) || !StringUtils.hasText(version)) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "name and version objects cannot be null");
        }
        return apiDefinitionRepository.existsByNameAndVersion(name, version);
    }

    private boolean definitionExists(Long standardDefinitionId) {
        return standardDefinitionId != null
                && apiDefinitionRepository.existsById(standardDefinitionId);
    }

    // -------------------------
    // search methods
    // -------------------------

    public ApiDefinition searchDefinition(Long definitionId) {
        ApiDefinition definition = null;
        if (definitionId == null) {
            throw new BadRequestException(
                    OpenDataMeshAPIStandardError.SC400_09_STDDEF_ID_IS_EMPTY,
                    "Definition id cannot be empty");
        }

        try {
            definition = loadDefinition(definitionId);
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading definition [" + definitionId + "]",
                    t);
        }

        return definition;
    }

     public ApiDefinition searchDefinition(ApiDefinition definition) {
        definition = resolveNameAndVersion(definition);
        return searchDefinition(definition.getName(), definition.getVersion());
    }
    /**
     * @return The definition identified by name and version. Null if not exists
     */
    public ApiDefinition searchDefinition(
            String name,
            String version) {

        ApiDefinition definition = null;
        List<ApiDefinition> definitions = searchDefinitions(name, version, null, null, null);
        if (definitions == null || definitions.size() == 0) {
            definition = null;
        } else if (definitions.size() == 1) {
            definition = definitions.get(0);
        } else {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching definitions");
        }

        return definition;
    }

    public List<ApiDefinition> searchDefinitions(
            String name,
            String version,
            String type,
            String specification,
            String specificationVersion) {
        List<ApiDefinition> definitionSearchResults = null;
        try {
            definitionSearchResults = findDefinitions(name, version, type, specification, specificationVersion);
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching definitions",
                    t);
        }
        return definitionSearchResults;
    }

    private List<ApiDefinition> findDefinitions(
            String name,
            String version,
            String type,
            String specification,
            String specificationVersion) {

        return apiDefinitionRepository
                .findAll(ApiDefinitionRepository.Specs.hasMatch(name, version, type, specification, specificationVersion));
    }

    // ======================================================================================
    // UPDATE
    // ======================================================================================

    public ApiDefinition updateDefinition(ApiDefinition definition) {
        if (definition == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Definition object cannot be null");
        }

        if (!definitionExists(definition.getId())) {
            throw new NotFoundException(
                    OpenDataMeshAPIStandardError.SC404_03_STDDEF_NOT_FOUND,
                    "Definition [" + definition.getId() + "] does not exist");
        }

        try {
            definition = saveDefinition(definition);
            logger.info("Definition [" + definition.getId() + "] successfully updated");
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while updating definition [" + definition.getId() + "]",
                    t);
        }

        return definition;
    }

    // ======================================================================================
    // DELETE
    // ======================================================================================

    public void deleteDefinition(Long definitionId) {
        ApiDefinition definition = searchDefinition(definitionId);
        if (definition == null) {
            throw new NotFoundException(
                    OpenDataMeshAPIStandardError.SC404_03_STDDEF_NOT_FOUND,
                    "Definition [" + definitionId + "] does not exist");
        }

        try {
            apiDefinitionRepository.delete(definition);
            logger.info("Definition [" + definitionId + "] successfully deleted");
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while deleting definition [" + definitionId + "]",
                    t);
        }

    }

}
