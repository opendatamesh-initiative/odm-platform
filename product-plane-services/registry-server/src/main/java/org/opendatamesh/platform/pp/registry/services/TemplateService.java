package org.opendatamesh.platform.pp.registry.services;

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
                    OpenDataMeshAPIStandardError.SC422_11_TEMPLATE_DOC_SYNTAX_IS_INVALID,
                    "Template href property cannot be empty");
        }

        if (templateExists(template.getMediaType(), template.getHref())) {
            throw new UnprocessableEntityException(
                    OpenDataMeshAPIStandardError.SC422_10_TEMPLATE_ALREADY_EXISTS,
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
        //componentTemplateRepository.saveAndFlush(??);
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
                    OpenDataMeshAPIStandardError.SC404_04_TEMPLATE_NOT_FOUND,
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
                    OpenDataMeshAPIStandardError.SC404_04_TEMPLATE_NOT_FOUND,
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

    
    // -------------------------
    // search methods
    // -------------------------

    public Template searchTemplate(Long templateId) {
        Template template = null;
        if (templateId == null) {
            throw new BadRequestException(
                    OpenDataMeshAPIStandardError.SC400_13_TEMPLATE_ID_IS_EMPTY,
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

    public List<Template> searchTemplates(
            String mediaType
    ) {
        List<Template> templateSearchResults = null;
        try {
            templateSearchResults = findTemplates(mediaType);
        } catch (Throwable t) {
            throw new InternalServerException(
                    OpenDataMeshAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching definitions",
                    t);
        }
        return templateSearchResults;
    }

    private List<Template> findTemplates(
            String mediaType
    ) {
        return templateRepository.findAll(
                TemplateRepository.Specs.hasMatch(mediaType)
        );
    }

}
