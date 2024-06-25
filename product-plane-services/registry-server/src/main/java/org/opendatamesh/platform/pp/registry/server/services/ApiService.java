package org.opendatamesh.platform.pp.registry.server.services;

import org.opendatamesh.dpds.model.core.EntityTypeDPDS;
import org.opendatamesh.dpds.parser.IdentifierStrategy;
import org.opendatamesh.platform.core.commons.servers.exceptions.*;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.Api;
import org.opendatamesh.platform.pp.registry.server.database.entities.ApiToSchemaRelationship;
import org.opendatamesh.platform.pp.registry.server.database.repositories.ApiRepository;
import org.opendatamesh.platform.pp.registry.server.database.repositories.ApiToSchemaRelationshipRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ApiService {

    @Autowired
    private ApiRepository apiRepository;

    @Autowired
    ApiToSchemaRelationshipRepository apiToSchemaRelationshipRepository;

    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    public ApiService() {

    }

    // ======================================================================================
    // CREATE
    // ======================================================================================

    public Api createApi(Api api) {

        if (api == null) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Api object cannot be null");
        }

        if (!StringUtils.hasText(api.getName())) {
            throw new UnprocessableEntityException(
                    RegistryApiStandardErrors.SC422_08_API_NOT_VALID,
                    "Property [name] cannot be empty");
        }

        if (!StringUtils.hasText(api.getVersion())) {
            throw new UnprocessableEntityException(
                    RegistryApiStandardErrors.SC422_08_API_NOT_VALID,
                    "Property [version] cannot be empty");
        }

        if (!StringUtils.hasText(api.getDefinition())) {
            throw new UnprocessableEntityException(
                    RegistryApiStandardErrors.SC422_08_API_NOT_VALID,
                    "Property [definition] cannot be empty");
        }

        
        String fqn = IdentifierStrategy.DEFUALT.getExternalComponentFqn(
                EntityTypeDPDS.API,
                api.getName(),
                api.getVersion());

        String id = IdentifierStrategy.DEFUALT.getId(fqn);
        
        api.setId(id);
        api.setFullyQualifiedName(fqn);
        api.setEntityType(EntityTypeDPDS.API.propertyValue());
        

        if (apiExists(id)) {
            throw new UnprocessableEntityException(
                    RegistryApiStandardErrors.SC422_07_API_ALREADY_EXISTS,
                    "Api [" + api.getName() + "(v. " + api.getVersion() + ")] already exists");
        }

        try {
            api = saveApi(api);
            logger.info("Api [" + api.getId() + "] successfully created");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while saving api",
                    t);
        }

        return api;
    }

    private Api saveApi(Api api) {
        return apiRepository.saveAndFlush(api);
    }

    // ======================================================================================
    // READ
    // ======================================================================================

    public Api readOneApi(Api api) {
        if (api == null) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Api object cannot be null");
        }

        return readApi(api.getId());
    }

    public Api readApi(String id) {
        Api api = null;

        api = searchApi(id);

        if (api == null) {
            throw new NotFoundException(
                    RegistryApiStandardErrors.SC404_03_API_NOT_FOUND,
                    "Api [" + id + "] does not exist");
        }

        return api;
    }

    public List<Long> getApiEndpointSchemaIds(String id) {
        if(!apiExists(id)) {
            throw new NotFoundException(
                    RegistryApiStandardErrors.SC404_03_API_NOT_FOUND,
                    "API definition with id [" + id + "] does not exists"
            );
        }

        List<ApiToSchemaRelationship> apiToSchemaRelationships = apiToSchemaRelationshipRepository.findByIdApiId(id);
        return apiToSchemaRelationships.stream().map(rel -> rel.getSchemaId()).collect(Collectors.toList());
    }

    public Api loadDefinition(String definitionId) {
        Api definition = null;
        Optional<Api> referenceObjectLookUpResults = apiRepository.findById(definitionId);

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
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "name and version objects cannot be null");
        }
        return apiRepository.existsByNameAndVersion(name, version);
    }

    private boolean apiExists(String id) {
        return id != null
                && apiRepository.existsById(id);
    }

    // -------------------------
    // search methods
    // -------------------------

    public Api searchApi(String definitionId) {
        Api definition = null;
        if (definitionId == null) {
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_09_API_ID_IS_EMPTY,
                    "Definition id cannot be empty");
        }

        try {
            definition = loadDefinition(definitionId);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while loading definition [" + definitionId + "]",
                    t);
        }

        return definition;
    }

    public Api searchDefinition(Api definition) {
        // definition = resolveNameAndVersion(definition);
        return searchDefinition(definition.getName(), definition.getVersion());
    }

    /**
     * @return The definition identified by name and version. Null if not exists
     */
    public Api searchDefinition(
            String name,
            String version) {

        Api definition = null;
        List<Api> definitions = searchDefinitions(name, version, null, null);
        if (definitions == null || definitions.size() == 0) {
            definition = null;
        } else if (definitions.size() == 1) {
            definition = definitions.get(0);
        } else {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while searching definitions");
        }

        return definition;
    }

    public List<Api> searchDefinitions(
            String name,
            String version,
            String specification,
            String specificationVersion) {
        List<Api> definitionSearchResults = null;
        try {
            definitionSearchResults = findDefinitions(name, version, specification, specificationVersion);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while searching definitions",
                    t);
        }
        return definitionSearchResults;
    }

    private List<Api> findDefinitions(
            String name,
            String version,
            String specification,
            String specificationVersion) {

        return apiRepository
                .findAll(ApiRepository.Specs.hasMatch(name, version, specification,
                        specificationVersion));
    }

    // ======================================================================================
    // UPDATE
    // ======================================================================================

    public Api updateDefinition(Api definition) {
        if (definition == null) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Definition object cannot be null");
        }

        if (!apiExists(definition.getId())) {
            throw new NotFoundException(
                    RegistryApiStandardErrors.SC404_03_API_NOT_FOUND,
                    "Definition [" + definition.getId() + "] does not exist");
        }

        try {
            definition = saveApi(definition);
            logger.info("Definition [" + definition.getId() + "] successfully updated");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while updating definition [" + definition.getId() + "]",
                    t);
        }

        return definition;
    }

    // ======================================================================================
    // DELETE
    // ======================================================================================

    public void deleteDefinition(String definitionId) {
        Api definition = searchApi(definitionId);
        if (definition == null) {
            throw new NotFoundException(
                    RegistryApiStandardErrors.SC404_03_API_NOT_FOUND,
                    "Definition [" + definitionId + "] does not exist");
        }

        try {
            apiRepository.delete(definition);
            logger.info("Definition [" + definitionId + "] successfully deleted");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while deleting definition [" + definitionId + "]",
                    t);
        }

    }

}
