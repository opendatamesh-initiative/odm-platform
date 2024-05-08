package org.opendatamesh.platform.pp.registry.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.*;
import org.opendatamesh.platform.core.dpds.model.core.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.parser.IdentifierStrategy;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.Template;
import org.opendatamesh.platform.pp.registry.server.database.repositories.TemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepository templateDefinitionRepository;

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    public TemplateService() {

    }

    public Template resolveNameAndVersion(Template template) {
        if (template == null) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Definition object cannot be null");
        }

        if (!StringUtils.hasText(template.getName())) {
            if (!StringUtils.hasText(template.getDefinition())) {
                throw new UnprocessableEntityException(
                        RegistryApiStandardErrors.SC422_14_TEMPLATE_NOT_VALID,
                        "Definition content property cannot be empty if name is also empty");
            }
            String fqn = UUID.nameUUIDFromBytes(template.getDefinition().getBytes()).toString();
            template.setName(fqn);
            logger.warn("Definition has no name. An UUID type-3 generated from its content will be used as name");
        }

        if (!StringUtils.hasText(template.getVersion())) {
            template.setVersion("1.0.0");
            logger.warn("Definition has no version. Version 1.0.0 will be used as default");
        }

        return template;
    }

    // ======================================================================================
    // CREATE
    // ======================================================================================

    public Template createTemplate(Template template) {

        if (template == null) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Standard definition object cannot be null");
        }

        if (!StringUtils.hasText(template.getName())) {
            throw new UnprocessableEntityException(
                    RegistryApiStandardErrors.SC422_14_TEMPLATE_NOT_VALID,
                    "Property [name] cannot be empty");
        }

        if (!StringUtils.hasText(template.getVersion())) {
            throw new UnprocessableEntityException(
                    RegistryApiStandardErrors.SC422_14_TEMPLATE_NOT_VALID,
                    "Property [version] cannot be empty");
        }

        if (!StringUtils.hasText(template.getDefinition())) {
            throw new UnprocessableEntityException(
                    RegistryApiStandardErrors.SC422_14_TEMPLATE_NOT_VALID,
                    "Property [definition] cannot be empty");
        }

         String fqn = IdentifierStrategy.DEFUALT.getExternalComponentFqn(
                EntityTypeDPDS.TEMPLATE,
                template.getName(),
                template.getVersion());

        String id = IdentifierStrategy.DEFUALT.getId(fqn);
        
        template.setId(id);
        template.setFullyQualifiedName(fqn);
        template.setEntityType(EntityTypeDPDS.TEMPLATE.propertyValue());

        if (templateExists(id)) {
            throw new UnprocessableEntityException(
                    RegistryApiStandardErrors.SC422_13_TEMPLATE_ALREADY_EXISTS,
                    "Template [" + template.getName() + "(v. " + template.getVersion() + ")] already exists");
        }

        try {
            template = saveTemplate(template);
            logger.info("Template [" + template.getId() + "] successfully created");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while saving template",
                    t);
        }

        return template;
    }

    private Template saveTemplate(Template template) {
        return templateDefinitionRepository.saveAndFlush(template);
    }

    // ======================================================================================
    // READ
    // ======================================================================================

    public Template readOneDefinition(Template definition) {
        if (definition == null) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Definition object cannot be null");
        }

        return readDefinition(definition.getId());
    }

    public Template readDefinition(String definitionId) {
        Template definition = null;

        definition = searchDefinition(definitionId);

        if (definition == null) {
            throw new NotFoundException(
                    RegistryApiStandardErrors.SC404_05_TEMPLATE_NOT_FOUND,
                    "Definition [" + definitionId + "] does not exist");
        }

        return definition;
    }

    public Template loadDefinition(String definitionId) {
        Template definition = null;
        Optional<Template> referenceObjectLookUpResults = templateDefinitionRepository.findById(definitionId);

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
        return templateDefinitionRepository.existsByNameAndVersion(name, version);
    }

    private boolean templateExists(String standardDefinitionId) {
        return standardDefinitionId != null
                && templateDefinitionRepository.existsById(standardDefinitionId);
    }

    // -------------------------
    // search methods
    // -------------------------

    public Template searchDefinition(String definitionId) {
        Template definition = null;
        if (definitionId == null) {
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_14_TEMPLATE_IS_EMPTY,
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

    public Template searchDefinition(Template definition) {
        definition = resolveNameAndVersion(definition);
        return searchDefinition(definition.getName(), definition.getVersion());
    }

    /**
     * @return The definition identified by name and version. Null if not exists
     */
    public Template searchDefinition(
            String name,
            String version) {

        Template definition = null;
        List<Template> definitions = searchDefinitions(name, version, null, null);
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

    public List<Template> searchDefinitions(
            String name,
            String version,
            String specification,
            String specificationVersion) {
        List<Template> definitionSearchResults = null;
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

    private List<Template> findDefinitions(
            String name,
            String version,
            String specification,
            String specificationVersion) {

        return templateDefinitionRepository
                .findAll(TemplateRepository.Specs.hasMatch(name, version, specification,
                        specificationVersion));
    }

    // ======================================================================================
    // UPDATE
    // ======================================================================================

    public Template updateDefinition(Template definition) {
        if (definition == null) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Definition object cannot be null");
        }

        if (!templateExists(definition.getId())) {
            throw new NotFoundException(
                    RegistryApiStandardErrors.SC404_05_TEMPLATE_NOT_FOUND,
                    "Definition [" + definition.getId() + "] does not exist");
        }

        try {
            definition = saveTemplate(definition);
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
        Template definition = searchDefinition(definitionId);
        if (definition == null) {
            throw new NotFoundException(
                    RegistryApiStandardErrors.SC404_05_TEMPLATE_NOT_FOUND,
                    "Definition [" + definitionId + "] does not exist");
        }

        try {
            templateDefinitionRepository.delete(definition);
            logger.info("Definition [" + definitionId + "] successfully deleted");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while deleting definition [" + definitionId + "]",
                    t);
        }

    }

}
