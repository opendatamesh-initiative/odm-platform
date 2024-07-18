package org.opendatamesh.platform.pp.blueprint.server.controllers;

import org.opendatamesh.platform.pp.blueprint.api.controllers.BlueprintAbstractController;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintSearchOptions;
import org.opendatamesh.platform.pp.blueprint.api.resources.ConfigResource;
import org.opendatamesh.platform.pp.blueprint.server.database.mappers.BlueprintMapper;
import org.opendatamesh.platform.pp.blueprint.server.services.BlueprintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class BlueprintController extends BlueprintAbstractController {

    @Autowired
    BlueprintMapper blueprintMapper;

    @Autowired
    BlueprintService blueprintService;

    @Override
    public List<BlueprintResource> readBlueprints(BlueprintSearchOptions blueprintSearchOptions) {
        return blueprintMapper.toResources(blueprintService.readBlueprints(blueprintSearchOptions));
    }

    @Override
    public BlueprintResource readBlueprint(Long id) {
        return blueprintMapper.toResource(blueprintService.readOneBlueprint(id));
    }

    @Override
    public BlueprintResource createBlueprint(BlueprintResource blueprint, Boolean checkBlueprint) throws IOException {
        if(checkBlueprint == null)
            checkBlueprint = true;
        return blueprintMapper.toResource(
                blueprintService.createBlueprint(blueprintMapper.toEntity(blueprint), checkBlueprint)
        );
    }

    @Override
    public BlueprintResource updateBlueprint(Long id, BlueprintResource blueprint) {
        return blueprintMapper.toResource(blueprintService.updateBlueprint(id, blueprintMapper.toEntity(blueprint)));
    }

    @Override
    public void deleteBlueprint(Long id) {
        blueprintService.deleteBlueprint(id);
    }

    @Override
    public void instanceBlueprint(Long blueprintId, ConfigResource configResource) {
        blueprintService.instanceBlueprint(blueprintId, configResource);
    }

}
