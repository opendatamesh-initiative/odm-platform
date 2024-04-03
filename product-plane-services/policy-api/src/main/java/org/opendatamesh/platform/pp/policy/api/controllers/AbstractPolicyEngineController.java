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
import org.opendatamesh.platform.pp.policy.api.clients.PolicyEngineClient;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/policy-engines"
)
@Validated
@Tag(
        name = "PolicyEngines",
        description = "Endpoints associated to PolicyEngines"
)
public abstract class AbstractPolicyEngineController implements PolicyEngineClient {

    // ===============================================================================
    // Resource examples
    // ===============================================================================

    private static final String EXAMPLE_POLICY_ENGINE = "{\n" + //
            "   \"id\": 1,\n" + //
            "   \"name\": \"opa-policy-checker\",\n" + //
            "   \"displayName\": \"OPA Policy Checker\",\n" + //
            "   \"adapterUrl\": \"http://localhost:9001/api/v1/up/policy-engine-adapter\",\n" + //
            "   \"createdAt\": \"a\",\n" + //
            "   \"updatedAt\": \"a\"\n" + //
            "}";

    private static final String EXAMPLE_POLICY_ENGINE_CREATE = "{\n" + //
            "   \"name\": \"opa-policy-checker\",\n" + //
            "   \"displayName\": \"OPA Policy Checker\",\n" + //
            "   \"adapterUrl\": \"http://localhost:9001/api/v1/up/policy-engine-adapter\"\n" + //
            "}";

    private static final String EXAMPLE_POLICY_ENGINE_UPDATE = "{\n" + //
            "   \"id\": 1,\n" + //
            "   \"name\": \"opa-policy-checker\",\n" + //
            "   \"displayName\": \"OPA Policy Checker\",\n" + //
            "   \"adapterUrl\": \"http://localhost:9001/api/v1/up/policy-engine-adapter\",\n" + //
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
    public Page<PolicyEngineResource> getPolicyEnginesEndpoint(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            PolicyEngineSearchOptions searchOptions
    ) {
        return getPolicyEngines(pageable, searchOptions);
    }

    public abstract Page<PolicyEngineResource> getPolicyEngines(Pageable pageable, PolicyEngineSearchOptions searchOptions);


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
    public PolicyEngineResource getPolicyEngineEndpoint(
            @Parameter(description = "", required = true)
            @PathVariable(value = "id") Long id
    ) {
        return getPolicyEngine(id);
    }

    public abstract PolicyEngineResource getPolicyEngine(Long id);

    // ===============================================================================
    // POST /policy-engines
    // ===============================================================================

    @Operation(
            summary = "Create a PolicyEngine",
            description = "Create a single PolicyEngine"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "PolicyEngine created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PolicyEngineResource.class),
                            examples = {@ExampleObject(name = "engine1", value = EXAMPLE_POLICY_ENGINE)}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40001 - PolicyEngine is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42201 - PolicyEngine is invalid"
                            + "\r\n - Error Code 42205 - PolicyEngine already exists",
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
    public PolicyEngineResource createPolicyEngineEndpoint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A PolicyEngine JSON object",
                    content = @Content(examples = {@ExampleObject(
                            name = "policy-engine-example",
                            description = "Example of a PolicyEngine for OPA",
                            value = EXAMPLE_POLICY_ENGINE_CREATE
                    )}))
            @RequestBody(required = false) PolicyEngineResource policyEngine
    ) {
        return createPolicyEngine(policyEngine);
    }

    public abstract PolicyEngineResource createPolicyEngine(PolicyEngineResource policyEngineResource);


    // ===============================================================================
    // PUT /policy-engines/{id}
    // ===============================================================================

    @Operation(
            summary = "Update a PolicyEngine",
            description = "Update the given PolicyEngine"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "PolicyEngine updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PolicyEngineResource.class),
                            examples = {@ExampleObject(name = "engine1", value = EXAMPLE_POLICY_ENGINE)}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40001 - PolicyEngine is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - PolicyEngine not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42201 - PolicyEngine is invalid"
                            + "\r\n - Error Code 42202 - PolicyEngine already exists",
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
    public PolicyEngineResource modifyPolicyEngineEndpoint(
            @Parameter(description = "ID of the PolicyEngine to update")
            @PathVariable(value = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A PolicyEngine JSON object",
                    content = @Content(examples = {@ExampleObject(
                            name = "policy-engine-example",
                            description = "Example of a PolicyEngine for OPA",
                            value = EXAMPLE_POLICY_ENGINE_UPDATE
                    )}))
            @RequestBody(required = false) PolicyEngineResource policyEngine
    ) {
        return modifyPolicyEngine(id, policyEngine);
    }

    public abstract PolicyEngineResource modifyPolicyEngine(Long id, PolicyEngineResource policyEngine);


    // ===============================================================================
    // DELETE /policy-engines/{id}
    // ===============================================================================

    @Operation(
            summary = "Delete a PolicyEngine",
            description = "Delete a PolicyEngine given its ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested PolicyEngine was delete successfully"
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
    @DeleteMapping(
            value = "/{id}",
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public void deletePolicyEngineEndpoint(
            @Parameter(description = "ID of the PolicyEngine to delete")
            @PathVariable(value = "id") Long id
    ) {
        deletePolicyEngine(id);
    }

    public abstract void deletePolicyEngine(Long id);

}
