package org.opendatamesh.platform.pp.blueprint.server.controllers;

import org.opendatamesh.platform.pp.blueprint.api.controllers.BlueprintAbstractController;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.opendatamesh.platform.pp.blueprint.server.database.mappers.BlueprintMapper;
import org.opendatamesh.platform.pp.blueprint.server.services.BlueprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class BlueprintController extends BlueprintAbstractController {

    @Autowired
    BlueprintMapper blueprintMapper;

    @Autowired
    BlueprintService blueprintService;

    @Override
    public List<BlueprintResource> readBlueprints() {
        return blueprintMapper.toResources(blueprintService.readBlueprints());
    }

    @Override
    public BlueprintResource readBlueprint(Long id) {
        return blueprintMapper.toResource(blueprintService.readOneBlueprint(id));
    }

    @Override
    public BlueprintResource createBlueprint(BlueprintResource blueprint) {
        return blueprintMapper.toResource(blueprintService.createBlueprint(blueprintMapper.toEntity(blueprint)));
    }

    @Override
    public BlueprintResource updateBlueprint(BlueprintResource blueprint) {
        return blueprintMapper.toResource(blueprintService.updateBlueprint(blueprintMapper.toEntity(blueprint)));
    }

    @Override
    public void deleteBlueprint(Long id) {
        blueprintService.deleteBlueprint(id);
    }
}
