package org.opendatamesh.platform.pp.registry.server.controllers;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.api.controllers.AbstractApiController;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.Api;
import org.opendatamesh.platform.pp.registry.server.database.mappers.ApiDefinitionMapper;
import org.opendatamesh.platform.pp.registry.server.services.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ApiController extends AbstractApiController {

    @Autowired
    private ApiService apiDefinitionService;

    @Autowired
    private ApiDefinitionMapper definitionMapper;

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    public ApiController() {
        logger.debug("Standard api definitions controller successfully started");
    }

    @Override
    public ExternalComponentResource createApi(
            ExternalComponentResource apiRes) {

        if (apiRes == null) {
            throw new BadRequestException(
                    RegistryApiStandardErrors.SC400_08_API_IS_EMPTY,
                    "API definition cannot be empty");
        }

        Api apiDefinition = definitionMapper.toEntity(apiRes);
        apiDefinition = apiDefinitionService.createApi(apiDefinition);
        return definitionMapper.toResource(apiDefinition);
    }

    @Override
    public List<ExternalComponentResource> getApis(
            String name,
            String version,
            String specification,
            String specificationVersion) {

        List<Api> definitions = apiDefinitionService.searchDefinitions(name, version, specification,
                specificationVersion);
        List<ExternalComponentResource> definitionResources = definitionMapper.definitionsToResources(definitions);
        return definitionResources;
    }

    @Override
    public ExternalComponentResource getApi(String id) {
        Api apiDefinition = apiDefinitionService.readApi(id);
        return definitionMapper.toResource(apiDefinition);
    }

    @Override
    public void deleteApi(String id) {
        apiDefinitionService.deleteDefinition(id);
    }

}
