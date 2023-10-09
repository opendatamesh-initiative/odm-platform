package org.opendatamesh.platform.pp.blueprint.api.controllers;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/blueprints"
)
@Validated
@Tag(
        name = "Blueprints",
        description = "Endpoints associated to blueprints collection"
)
public abstract class BlueprintAbstractController {
}
