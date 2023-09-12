package org.opendatamesh.platform.pp.registry.server.controllers;

import java.util.List;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.api.controllers.AbstractTemplateController;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.Template;
import org.opendatamesh.platform.pp.registry.server.database.mappers.TemplateDefinitionMapper;
import org.opendatamesh.platform.pp.registry.server.services.TemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TemplateController extends AbstractTemplateController {

    @Autowired
    private TemplateService templateDefinitionService;

    @Autowired
    private TemplateDefinitionMapper templateDefinitionMapper;

    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);

    public TemplateController() {
        logger.debug("Template controller successfully started");
    }

    @Override
    public ExternalComponentResource createTemplate(ExternalComponentResource templateRes) {

        if (templateRes == null) {
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_14_TEMPLATE_IS_EMPTY,
                    "Template definition cannot be empty");
        }

        Template templateDefinition = templateDefinitionMapper.toEntity(templateRes);
        templateDefinition = templateDefinitionService.createTemplate(templateDefinition);
        return templateDefinitionMapper.toResource(templateDefinition);
    }

    @Override
    public List<ExternalComponentResource> getTemplates(
            String name,
            String version,
            String specification,
            String specificationVersion) {

        List<Template> definitions = templateDefinitionService.searchDefinitions(name, version, specification,
                specificationVersion);
        List<ExternalComponentResource> definitionResources = templateDefinitionMapper
                .definitionsToResources(definitions);
        return definitionResources;
    }

    @Override
    public ExternalComponentResource getTemplate(String id) {
        Template definition = templateDefinitionService.readDefinition(id);
        ExternalComponentResource definitionResource = templateDefinitionMapper.toResource(definition);
        return definitionResource;
    }

    @Override
    public void deleteTemplate(String id) {
        templateDefinitionService.deleteDefinition(id);
    }

}
