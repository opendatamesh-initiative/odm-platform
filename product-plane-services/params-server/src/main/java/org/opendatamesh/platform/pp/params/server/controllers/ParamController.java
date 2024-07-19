package org.opendatamesh.platform.pp.params.server.controllers;

import org.opendatamesh.platform.pp.params.api.controllers.AbstractParamController;
import org.opendatamesh.platform.pp.params.api.resources.ParamResource;
import org.opendatamesh.platform.pp.params.server.database.mappers.ParamMapper;
import org.opendatamesh.platform.pp.params.server.services.ParamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ParamController extends AbstractParamController {

    @Autowired
    ParamMapper paramMapper;

    @Autowired
    ParamService paramService;

    @Override
    public ParamResource createParam(ParamResource param) {
        return paramMapper.toResource(paramService.createParam(paramMapper.toEntity(param)));
    }

    @Override
    public ParamResource updateParam(Long id, ParamResource param) {
        return paramMapper.toResource(paramService.updateParam(id, paramMapper.toEntity(param)));
    }

    @Override
    public List<ParamResource> getParams(String clientUUID) {
        return paramMapper.toResources(paramService.readParams(clientUUID));
    }

    @Override
    public ParamResource getParamByName(String name, String clientUUID) {
        return paramMapper.toResource(paramService.readOneParamByName(name, clientUUID));
    }

    @Override
    public ParamResource getParam(Long id, String clientUUID) {
        return paramMapper.toResource(paramService.readOneParam(id, clientUUID));
    }

    @Override
    public void deleteParam(Long id) {
        paramService.deleteParam(id);
    }

}
