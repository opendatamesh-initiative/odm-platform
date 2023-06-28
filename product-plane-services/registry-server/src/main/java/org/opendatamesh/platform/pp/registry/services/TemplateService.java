package org.opendatamesh.platform.pp.registry.services;

import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ApiToSchemaRelationship;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ComponentTemplate;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Definition;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Template;
import org.opendatamesh.platform.pp.registry.database.repositories.ComponentTemplateRepository;
import org.opendatamesh.platform.pp.registry.database.repositories.TemplateRepository;
import org.opendatamesh.platform.pp.registry.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private ComponentTemplateRepository componentTemplateRepository;

    private static final Logger logger = LoggerFactory.getLogger(TemplateService.class);

    public TemplateService() {

    }

    // ======================================================================================
    // CREATE
    // ======================================================================================

    public Template createTemplate(Template template) {

        if (template == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Template object cannot be null");
        }

        if (!StringUtils.hasText(template.getHref())) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_14_TEMPLATE_DOC_SYNTAX_IS_INVALID,
                    "Template href property cannot be empty");
        }

        if (templateExists(template.getMediaType(), template.getHref())) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_13_TEMPLATE_ALREADY_EXISTS,
                    "Template [" + template.getMediaType() + "(v. " + template.getHref() + ")] already exists");
        }

        try {
            template = saveTemplate(template);
            logger.info("Template [" + template.getId() + "] successfully created");
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving template",
                    t);
        }

        return template;

    }

    private Template saveTemplate(Template template) {
        return templateRepository.saveAndFlush(template);
    }


    // ======================================================================================
    // READ
    // ======================================================================================

    public Template readOneTemplate(Template template) {
        if (template == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Template object cannot be null");
        }

        return readTemplate(template.getId());
    }

    public Template readTemplate(Long templateId) {

        Template template = null;

        template = searchTemplate(templateId);

        if (template == null) {
            throw new NotFoundException(
                    OpenDataMeshAPIStandardError.SC404_05_TEMPLATE_NOT_FOUND,
                    "Template [" + templateId + "] does not exist");
        }

        return template;
    }

    public Template loadTemplate(Long templateId) {
        Template template = null;
        Optional<Template> referenceObjectLookUpResults = templateRepository.findById(templateId);

        if (referenceObjectLookUpResults.isPresent()) {
            template = referenceObjectLookUpResults.get();
        }
        return template;
    }

    
    // ======================================================================================
    // DELETE
    // ======================================================================================

    public void deleteTemplate(Long templateId) {
        Template template = searchTemplate(templateId);
        if (template == null) {
            throw new NotFoundException(
                    OpenDataMeshAPIStandardError.SC404_05_TEMPLATE_NOT_FOUND,
                    "Template [" + templateId + "] does not exist");
        }

        try {
            templateRepository.delete(template); // Delete also ComponentTemplate ?
            logger.info("Template [" + templateId + "] successfully deleted");
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while deleting template [" + templateId + "]",
                    t);
        }

    }


    // -------------------------
    // exists methods
    // -------------------------

    private boolean templateExists(String mediaType, String href) {
        if (!StringUtils.hasText(mediaType) || !StringUtils.hasText(href)) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "mediaType and href objects cannot be null");
        }
        return templateRepository.existsByMediaTypeAndHref(mediaType, href);
    }

    private boolean templateExists(Long templateId) {
        return templateId != null
                && templateRepository.existsById(templateId);
    }

    private boolean relationshipExists(String componentId, Long templateId, String componentType, String infoType) {
        if (componentId == null ||  templateId == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "componentId and templateId properties cannot be null");
        }
        return componentTemplateRepository.existsByIdComponentIdAndIdTemplateIdAndIdComponentTypeAndIdInfoType(componentId, templateId, componentType, infoType);
    }

    
    // -------------------------
    // search methods
    // -------------------------

    public Template searchTemplate(Long templateId) {
        Template template = null;
        if (templateId == null) {
            throw new BadRequestException(
                    OpenDataMeshAPIStandardError.SC400_15_TEMPLATE_ID_IS_EMPTY,
                    "Template id cannot be empty");
        }

        try {
            template = loadTemplate(templateId);
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading template [" + templateId + "]",
                    t);
        }

        return template;
    }

    /**
     * @return The template identified by mediaType and href. Null if not exists
     */
    public Template searchTemplate(
            String mediaType,
            String href) {
        Template template = null;
        List<Template> templates = searchTemplates(mediaType, href);
        if (templates == null || templates.size() == 0) {
            template = null;
        } else if (templates.size() == 1) {
            template = templates.get(0);
        } else {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching definitions");
        }

        return template;
    }

    public List<Template> searchTemplates(
            String mediaType
    ) {
        List<Template> templateSearchResults = null;
        try {
            templateSearchResults = findTemplates(mediaType, null);
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching definitions",
                    t);
        }
        return templateSearchResults;
    }

    public List<Template> searchTemplates(
            String mediaType,
            String href
    ) {
        List<Template> templateSearchResults = null;
        try {
            templateSearchResults = findTemplates(mediaType, href);
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching definitions",
                    t);
        }
        return templateSearchResults;
    }

    private List<Template> findTemplates(
            String mediaType,
            String href
    ) {
        return templateRepository.findAll(
                TemplateRepository.Specs.hasMatch(mediaType, href)
        );
    }

    public ComponentTemplate createComponentTemplateRelationship(ComponentTemplate relationship) {

        if (relationship == null) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Relationship object cannot be null");
        }

        if (relationship.getId() == null) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_14_TEMPLATE_DOC_SYNTAX_IS_INVALID,
                    "Relationship id property cannot be empty");
        }

        if (relationship.getId().getComponentId() == null) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_14_TEMPLATE_DOC_SYNTAX_IS_INVALID,
                    "Relationship componentId property cannot be empty");
        }

        if (relationship.getId().getTemplateId() == null) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_14_TEMPLATE_DOC_SYNTAX_IS_INVALID,
                    "Relationship templateId property cannot be empty");
        }

        if (relationshipExists(relationship.getId().getComponentId(), relationship.getId().getTemplateId(), relationship.getId().getComponentType(), relationship.getId().getInfoType())) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_12_SCHEMA_TO_API_REL_ALREADY_EXISTS,
                    "Component [" + relationship.getId().getComponentId()+ " relationship with template " + relationship.getId().getTemplateId() + "] already exists");
        }

        try {
            relationship = saveRelationship(relationship);
            logger.info("Component [" + relationship.getId().getComponentId()+ " relationship with template [" + relationship.getId().getTemplateId() + "] successfully created");
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving relationship",
                    t);
        }

        return relationship;
    }

    private ComponentTemplate saveRelationship(ComponentTemplate relationship) {
        return componentTemplateRepository.saveAndFlush(relationship);
    }

}
