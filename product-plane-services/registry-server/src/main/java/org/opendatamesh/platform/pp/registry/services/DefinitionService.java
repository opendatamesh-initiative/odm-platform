package org.opendatamesh.platform.pp.registry.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.persistence.criteria.Predicate;

import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.ReferenceObject;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.StandardDefinition;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Definition;
import org.opendatamesh.platform.pp.registry.database.repositories.DefinitionRepository;
import org.opendatamesh.platform.pp.registry.database.repositories.StandardDefinitionRepository;
import org.opendatamesh.platform.pp.registry.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.registry.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.exceptions.UnprocessableEntityException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class DefinitionService {

    @Autowired
    private DefinitionRepository definitionRepository;

    private static final Logger logger = LoggerFactory.getLogger(DefinitionService.class);

    public DefinitionService() {

    }

    // ======================================================================================
    // CREATE
    // ======================================================================================

    public Definition createDefinition(Definition definition) {

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

        if (!StringUtils.hasText(definition.getName())) {
            String fqn = UUID.nameUUIDFromBytes(definition.getContent().getBytes()).toString();
            definition.setName(fqn);
            logger.warn("Definition has no name. An UUID type-3 generated from its content will be used as name");
        }

        if (!StringUtils.hasText(definition.getVersion())) {
            definition.setVersion("1.0.0");
            logger.warn("Definition has no version. Version 1.0.0 will be used as default");
        }

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

    private Definition saveDefinition(Definition definition) {
        return definitionRepository.saveAndFlush(definition);
    }

    // ======================================================================================
    // READ
    // ======================================================================================

    public Definition readOneDefinition(Definition definition) {
        if (definition == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Definition object cannot be null");
        }

        return readDefinition(definition.getId());
    }

    public Definition readDefinition(Long definitionId) {
        Definition definition = null;

        definition = searchDefinition(definitionId);

        if (definition == null) {
            throw new NotFoundException(
                    OpenDataMeshAPIStandardError.SC404_03_STDDEF_NOT_FOUND,
                    "Definition [" + definitionId + "] does not exist");
        }

        return definition;
    }

    public Definition loadDefinition(Long definitionId) {
        Definition definition = null;
        Optional<Definition> referenceObjectLookUpResults = definitionRepository.findById(definitionId);

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
        return definitionRepository.existsByNameAndVersion(name, version);
    }

    private boolean definitionExists(Long standardDefinitionId) {
        return standardDefinitionId != null
                && definitionRepository.existsById(standardDefinitionId);
    }

    // -------------------------
    // search methods
    // -------------------------

    public Definition searchDefinition(Long definitionId) {
        Definition definition = null;
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

    /**
     * @return The definition identified by name and version. Null if not exists
     */
    public Definition searchDefinition(
            String name,
            String version) {
        Definition definition = null;
        List<Definition> definitions = searchDefinitions(name, version, null, null, null);
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

    public List<Definition> searchDefinitions(
            String name,
            String version,
            String type,
            String specification,
            String specificationVersion) {
        List<Definition> definitionSearchResults = null;
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

    private List<Definition> findDefinitions(
            String name,
            String version,
            String type,
            String specification,
            String specificationVersion) {

        return definitionRepository
                .findAll(DefinitionRepository.Specs.hasMatch(name, version, type, specification, specificationVersion));
    }

    // ======================================================================================
    // UPDATE
    // ======================================================================================

    public Definition updateDefinition(Definition definition) {
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
        Definition definition = searchDefinition(definitionId);
        if (definition == null) {
            throw new NotFoundException(
                    OpenDataMeshAPIStandardError.SC404_03_STDDEF_NOT_FOUND,
                    "Definition [" + definitionId + "] does not exist");
        }

        try {
            definitionRepository.delete(definition);
            logger.info("Definition [" + definitionId + "] successfully deleted");
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while deleting definition [" + definitionId + "]",
                    t);
        }

    }

}
