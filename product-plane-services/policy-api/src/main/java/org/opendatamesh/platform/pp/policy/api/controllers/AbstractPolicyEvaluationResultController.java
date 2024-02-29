package org.opendatamesh.platform.pp.policy.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
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
public abstract class AbstractPolicyEvaluationResultController {

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
    // GET /policy-engines
    // ===============================================================================

    @Operation(
            summary = "Get all PolicyEngines",
            description = "Get all the registered PolicyEngine paginated"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All the PolicyEngines",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    array = @ArraySchema(schema = @Schema(implementation = PolicyEngineResource.class))),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    array = @ArraySchema(schema = @Schema(implementation = PolicyEngineResource.class))),
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PolicyEngineResource.class)))
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

    public abstract Page<PolicyEvaluationResultResource> getPolicyEvaluationResults(
            Pageable pageable, PolicyEvaluationResultSearchOptions searchOptions
    );


    // ===============================================================================
    // GET /policy-engines/{id}
    // ===============================================================================

    @Operation(
            summary = "Get a PolicyEngine",
            description = "Get the PolicyEngine identified by the given ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested PolicyEngine",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    schema = @Schema(implementation = PolicyEngineResource.class)),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    schema = @Schema(implementation = PolicyEngineResource.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PolicyEngineResource.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - PolicyEngine not found",
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

    public abstract PolicyEvaluationResultResource getPolicyEvaluationResult(Long id);

    @PostMapping
    public abstract PolicyEvaluationResultResource createPolicyEvaluationResult(
            @Parameter(description = "")
            @RequestBody PolicyEvaluationResultResource policyEvaluationResult
    );

    @PutMapping(value = "/{id}")
    public abstract PolicyEvaluationResultResource modifyPolicyEvaluationResult(
            @Parameter(description = "")
            @PathVariable(value = "id") Long id,
            @Parameter(description = "")
            @RequestBody PolicyEvaluationResultResource policyEvaluationResult
    );

    @DeleteMapping(value = "/{id}")
    public abstract PolicyEvaluationResultResource deletePolicyEvaluationResult(
            @Parameter(description = "")
            @PathVariable(value = "id") Long id
    );

}
