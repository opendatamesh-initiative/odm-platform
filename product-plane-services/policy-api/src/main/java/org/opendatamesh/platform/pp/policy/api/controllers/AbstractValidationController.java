package org.opendatamesh.platform.pp.policy.api.controllers;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(
        value = "/validation"
)
@Validated
@Tag(
        name = "Validation",
        description = "Endpoints associated to Policy validation"
)
public abstract class AbstractValidationController {

    @PostMapping
    public abstract PolicyEvaluationResultResource validateObject(
            @Parameter(description = "")
            @RequestBody EventResource eventResource
    );

}