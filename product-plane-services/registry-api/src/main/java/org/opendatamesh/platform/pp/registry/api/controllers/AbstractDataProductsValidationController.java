package org.opendatamesh.platform.pp.registry.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductValidationRequestResource;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductValidationResponseResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(
        value = "/products",
        produces = {
                "application/vnd.odmp.v1+json",
                "application/vnd.odmp+json",
                "application/json"
        }
)
@Validated
@Tag(
        name = "Data Products",
        description = "Endpoints associated to products collection")
public interface AbstractDataProductsValidationController {

    @PostMapping("/*/validate")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Validate a data product descriptor",
            description = "Validate a data product descriptor using parser and policies"
    )
    DataProductValidationResponseResource validateDataProduct(
            @RequestBody
            DataProductValidationRequestResource validationRequestResource
    );
}
