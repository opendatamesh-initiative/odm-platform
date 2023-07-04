package org.opendatamesh.platform.up.policy.api.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.up.policy.api.v1.resources.ErrorResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.ValidateResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(
        value = "/validate",
        produces = { "application/json" }
)
@Validated
@Tag(
        name = "Policies validation API",
        description = "API to validate one or more policies for a document"
)
public abstract class AbstractValidateController {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Validate a document",
            description = "Validate a document with a single policy, multiple policies, all the policies or the policies in a suite"
                    + "\n\n If neither the optional parameters \"id\" or \"suite\" will be used the document"
                    + "will be validated through all the policies sotred on OPA Server",
            tags = { "Policies validation API" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Document validated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidateResponse.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Error code 40006 - OPA Server bad request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Error code 50000 - Generic internal server error"
                            + "\n\nError code 50001 - OPA Server internal server error"
                            + "\n\nError code 50002 - OPA Server not reachable",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResource.class)
                    )
            )
    })
    public ResponseEntity validate(
            @Parameter(description = "Optional - identifier/s of the policy or the policies to use in the validation of the document")
            @Valid @RequestParam(name = "id", required = false) String[] ids,
            @Parameter(description = "Optional - identifier/s of the suite or the suites to use in the validation of the document")
            @Valid @RequestParam(name = "suite", required = false) String[] suites,
            @Parameter(description = "JSON object of the document to be validated")
            @Valid @RequestBody Object document
    ){
        return validateDocument(ids, suites, document);
    }

    public abstract ResponseEntity validateDocument(String[] ids, String[] suites, Object document);

}
