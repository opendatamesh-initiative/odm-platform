package org.opendatamesh.platform.up.policy.api.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.up.policy.api.v1.resources.DocumentResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.EvaluationResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(
        value = "/evaluate-policy",
        produces = { "application/json" }
)
@Validated
@Tag(
        name = "Policies evaluation API",
        description = "API to evaluate one policy for a given object"
)
public abstract class AbstractPolicyEngineController implements PolicyEngineController {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Evaluate an object",
            description = "Evaluate an object against the provided policy",
            tags = { "Policies evaluation API" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Document evaluated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EvaluationResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad request",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorRes.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Generic internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorRes.class)
                    )
            )
    })
    public EvaluationResource evaluate(
            @Parameter(description = "JSON object containing the object to be evaluated and the policy to validate against")
            @Valid @RequestBody DocumentResource document
    ){
        return evaluateDocument(document);
    }

    public abstract EvaluationResource evaluateDocument(DocumentResource document);

}
