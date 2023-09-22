package org.opendatamesh.platform.pp.devops.server.controllers;

import org.opendatamesh.platform.pp.devops.api.controllers.AbstractLifecycleController;
import org.opendatamesh.platform.pp.devops.api.resources.LifecycleResource;
import org.opendatamesh.platform.pp.devops.server.database.mappers.LifecycleMapper;
import org.opendatamesh.platform.pp.devops.server.services.LifecycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LifecycleController extends AbstractLifecycleController {

    @Autowired
    LifecycleService lifecycleService;

    @Autowired
    LifecycleMapper lifecycleMapper;

    @Override
    public List<LifecycleResource> readLifecycles() {
        return lifecycleMapper.toResources(lifecycleService.getLifecycles());
    }

    @Override
    public List<LifecycleResource> readDataProductLifecycles(String dataProductId) {
        return lifecycleMapper.toResources(lifecycleService.getDataProductLifecycles(dataProductId));
    }

    @Override
    public List<LifecycleResource> readDataProductVersionLifecycles(String dataProductId, String versionNumber) {
        return lifecycleMapper.toResources(lifecycleService.getDataProductVersionLifecycles(dataProductId, versionNumber));
    }

    @Override
    public LifecycleResource readDataProductVersionCurrentLifecycle(String dataProductId, String versionNumber) {
        return lifecycleMapper.toResource(lifecycleService.getDataProductVersionCurrentLifecycle(dataProductId, versionNumber));
    }
}
