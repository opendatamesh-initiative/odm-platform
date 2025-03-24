package org.opendatamesh.platform.pp.policy.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.ValidationResponseResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@Validated
@Tag(name = "Validation", description = "Endpoints associated with Policy validation")
public interface AbstractValidationController {

    @Operation(
            summary = "Validate an input Event",
            description = "Validate the input Event with all the needed Policies"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Input object correctly evaluated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PolicyEngineResource.class),
                            examples = {@ExampleObject(name = "evaluation", value = "{\n" +
                                    "   \"id\": 1,\n" +
                                    "   \"dataProductId\": \"abc123\",\n" +
                                    "   \"dataProductVersion\": \"1.0.1\",\n" +
                                    "   \"inputObject\": \"{\\\"name\\\":\\\"dp-1\\\",\\\"description\\\":\\\"DataProduct1Draft\\\",\\\"domain\\\":\\\"Marketing\\\"}\",\n" +
                                    "   \"outputObject\": \"{\\\"allow\\\":true}\",\n" +
                                    "   \"result\": true,\n" +
                                    "   \"policyId\": 1,\n" +
                                    "   \"createdAt\": \"a\",\n" +
                                    "   \"updatedAt\": \"a\"\n" +
                                    "}")}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Input object is empty",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "Unprocessable Content - Input object is invalid",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal Server Error",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
            )
    })
    @PostMapping(
            path = "/validation",
            consumes = "application/json",
            produces = "application/json"
    )
    ValidationResponseResource validateInputObject(@Valid @NotNull @RequestBody PolicyEvaluationRequestResource evaluationRequest);

    @Operation(
            summary = "Test validation logic",
            description = "Endpoint for testing validation without storing policies evaluation results"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Validation test executed successfully",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ValidationResponseResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Bad Request - Invalid test input",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
            )
    })
    @PostMapping(
            path = "/validation-test",
            consumes = "application/json",
            produces = "application/json"
    )
    ValidationResponseResource testValidateInputObject(@Valid @NotNull @RequestBody PolicyEvaluationRequestResource testRequest);
}
