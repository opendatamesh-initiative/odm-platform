package org.opendatamesh.platform.pp.policy.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/policy-evaluation-results"
)
@Validated
@Tag(
        name = "Policy Evaluation Results",
        description = "Endpoints associated to Policy Evaluation Results"
)
public abstract class AbstractPolicyEvaluationResultController implements PolicyEvaluationResultController {

    // ===============================================================================
    // Resource examples
    // ===============================================================================

    private static final String EXAMPLE_POLICY_EVALUATION_RESULT = "{\n" + //
            "   \"id\": 1,\n" + //
            "   \"dataProductId\": \"abc123\",\n" + //
            "   \"dataProductVersion\": \"1.0.1\",\n" + //
            "   \"inputObject\": \"{\"name\":\"dp-1\",\"description\":\"DataProduct1Draft\",\"domain\":\"Marketing\"}\",\n" + //
            "   \"outputObject\": \"{\"allow\":true}\",\n" + //
            "   \"result\": true,\n" + //
            "   \"policyId\": 1,\n" + //
            "   \"createdAt\": \"a\",\n" + //
            "   \"updatedAt\": \"a\"\n" + //
            "}";

    private static final String EXAMPLE_POLICY_EVALUATION_RESULT_CREATE = "{\n" + //
            "   \"dataProductId\": \"abc123\",\n" + //
            "   \"dataProductVersion\": \"1.0.1\",\n" + //
            "   \"inputObject\": \"{\"name\":\"dp-1\",\"description\":\"DataProduct1Draft\",\"domain\":\"Marketing\"}\",\n" + //
            "   \"outputObject\": \"{\"allow\":true}\",\n" + //
            "   \"result\": true,\n" + //
            "   \"policyId\": 1\n" + //
            "}";

    private static final String EXAMPLE_POLICY_EVALUATION_RESULT_UPDATE = "{\n" + //
            "   \"id\": 1,\n" + //
            "   \"dataProductId\": \"abc123\",\n" + //
            "   \"dataProductVersion\": \"1.0.1\",\n" + //
            "   \"inputObject\": \"{\"name\":\"dp-1\",\"description\":\"DataProduct1Draft\",\"domain\":\"Marketing\"}\",\n" + //
            "   \"outputObject\": \"{\"allow\":true}\",\n" + //
            "   \"result\": true,\n" + //
            "   \"policyId\": 1,\n" + //
            "   \"createdAt\": \"a\"\n" + //
            "}";

    // ===============================================================================
    // GET /policy-evaluation-results
    // ===============================================================================

    @Operation(
            summary = "Get all PolicyEvaluationResults",
            description = "Get all the registered PolicyEvaluationResult paginated"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All the PolicyEvaluationResults",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    array = @ArraySchema(schema = @Schema(implementation = PolicyEvaluationResultResource.class))),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    array = @ArraySchema(schema = @Schema(implementation = PolicyEvaluationResultResource.class))),
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PolicyEvaluationResultResource.class)))
                    }
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    @GetMapping(
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public Page<PolicyEvaluationResultResource> getPolicyEvaluationResultsEndpoint(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            PolicyEvaluationResultSearchOptions searchOptions
    ) {
        return getPolicyEvaluationResults(pageable, searchOptions);
    }

    // ===============================================================================
    // GET /policy-evaluation-results/{id}
    // ===============================================================================

    @Operation(
            summary = "Get a PolicyEvaluationResult",
            description = "Get the PolicyEvaluationResult identified by the given ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested PolicyEvaluationResult",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    schema = @Schema(implementation = PolicyEvaluationResultResource.class)),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    schema = @Schema(implementation = PolicyEvaluationResultResource.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PolicyEvaluationResultResource.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - PolicyEvaluationResult not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    @GetMapping(
            value = "/{id}",
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public PolicyEvaluationResultResource getPolicyEvaluationResultEndpoint(
            @Parameter(description = "", required = true)
            @PathVariable(value = "id") Long id
    ) {
        return getPolicyEvaluationResult(id);
    }

    // ===============================================================================
    // POST /policy-evaluation-results
    // ===============================================================================

    @Operation(
            summary = "Create a PolicyEvaluationResult",
            description = "Create a single PolicyEvaluationResult"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "PolicyEvaluationResult created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PolicyEvaluationResultResource.class),
                            examples = {@ExampleObject(name = "evaluation", value = EXAMPLE_POLICY_EVALUATION_RESULT)}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40001 - PolicyEvaluationResult is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Parent Policy not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42201 - PolicyEvaluationResult is invalid",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    @PostMapping(
            consumes = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            },
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public PolicyEvaluationResultResource createPolicyEvaluationResultEndpoint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A PolicyEvaluationResult JSON object",
                    content = @Content(examples = {@ExampleObject(
                            name = "policy-evaluation-result-creation-example",
                            description = "Example of a PolicyEvaluationResult for OPA",
                            value = EXAMPLE_POLICY_EVALUATION_RESULT_CREATE
                    )}))
            @RequestBody(required = false) PolicyEvaluationResultResource policyEvaluationResult
    ) {
        return createPolicyEvaluationResult(policyEvaluationResult);
    }

    // ===============================================================================
    // PUT /policy-evaluation-results/{id}
    // ===============================================================================

    @Operation(
            summary = "Update a PolicyEvaluationResult",
            description = "Update the given PolicyEvaluationResult"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "PolicyEvaluationResult updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PolicyEvaluationResultResource.class),
                            examples = {@ExampleObject(name = "evaluation", value = EXAMPLE_POLICY_EVALUATION_RESULT)}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40001 - PolicyEvaluationResult is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - PolicyEvaluationResult not found"
                            + "\r\n - Error Code 40401 - Parent Policy not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42201 - PolicyEvaluationResult is invalid",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    @PutMapping(
            value = "/{id}",
            consumes = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            },
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public PolicyEvaluationResultResource modifyPolicyEvaluationResultEndpoint(
            @Parameter(description = "ID of the PolicyEvaluationResult to update")
            @PathVariable(value = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A PolicyEvaluationResult JSON object",
                    content = @Content(examples = {@ExampleObject(
                            name = "policy-evaluation-results-update-example",
                            description = "Example of a PolicyEvaluationResult for OPA",
                            value = EXAMPLE_POLICY_EVALUATION_RESULT_UPDATE
                    )}))
            @RequestBody(required = false) PolicyEvaluationResultResource policyEvaluationResult
    ) {
        return modifyPolicyEvaluationResult(id, policyEvaluationResult);
    }

    // ===============================================================================
    // DELETE /policy-engines/{id}
    // ===============================================================================

    @Operation(
            summary = "Delete a PolicyEvaluationResult",
            description = "Delete a PolicyEvaluationResult given its ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested PolicyEvaluationResult was delete successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - PolicyEvaluationResult not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    @DeleteMapping(
            value = "/{id}",
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public void deletePolicyEvaluationResultEndpoint(
            @Parameter(description = "ID of the PolicyEvaluationResult to delete")
            @PathVariable(value = "id") Long id
    ) {
        deletePolicyEvaluationResult(id);
    }
}
