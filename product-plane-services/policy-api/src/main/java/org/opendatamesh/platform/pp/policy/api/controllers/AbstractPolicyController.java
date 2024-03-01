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
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicySearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/policies"
)
@Validated
@Tag(
        name = "Policies",
        description = "Endpoints associated to Policies"
)
public abstract class AbstractPolicyController implements PolicyController {

    // ===============================================================================
    // Resource examples
    // ===============================================================================

    private static final String EXAMPLE_POLICY = "{\n" + //
            "   \"id\": 1,\n" + //
            "   \"name\": \"dataproduct-name-checker\",\n" + //
            "   \"displayName\": \"Data Product Name Checker\",\n" + //
            "   \"description\": \"Check whether the name of the input Data Product is compliant with global naming convention or not\",\n" + //
            "   \"blocking_flag\": true,\n" + //
            "   \"rawContent\": \"package dataproduct-name-checker\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}\",\n" + //
            "   \"suite\": \"CREATION\",\n" + //
            "   \"policyEngineId\": 1,\n" + //
            "   \"createdAt\": \"a\",\n" + //
            "   \"updatedAt\": \"a\"\n" + //
            "}";

    private static final String EXAMPLE_POLICY_CREATE = "{\n" + //
            "   \"name\": \"dataproduct-name-checker\",\n" + //
            "   \"displayName\": \"Data Product Name Checker\",\n" + //
            "   \"description\": \"Check whether the name of the input Data Product is compliant with global naming convention or not\",\n" + //
            "   \"blocking_flag\": true,\n" + //
            "   \"rawContent\": \"package dataproduct-name-checker\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}\",\n" + //
            "   \"suite\": \"CREATION\",\n" + //
            "   \"policyEngineId\": 1\n" + //
            "}";

    private static final String EXAMPLE_POLICY_UPDATE = "{\n" + //
            "   \"id\": 1,\n" + //
            "   \"name\": \"dataproduct-name-checker\",\n" + //
            "   \"displayName\": \"Data Product Name Checker\",\n" + //
            "   \"description\": \"Check whether the name of the input Data Product is compliant with global naming convention or not\",\n" + //
            "   \"blocking_flag\": true,\n" + //
            "   \"rawContent\": \"package dataproduct-name-checker\n\ndefault allow := false\n\nallow := true {                                     \n    startswith(input.name, \"dp-\")\n}\",\n" + //
            "   \"suite\": \"CREATION\",\n" + //
            "   \"policyEngineId\": 1,\n" + //
            "   \"createdAt\": \"a\"\n" + //
            "}";


    // ===============================================================================
    // GET /policies
    // ===============================================================================

    @Operation(
            summary = "Get all Policies",
            description = "Get all the registered Policy paginated"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All the Policies",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    array = @ArraySchema(schema = @Schema(implementation = PolicyResource.class))),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    array = @ArraySchema(schema = @Schema(implementation = PolicyResource.class))),
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = PolicyResource.class)))
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
    public Page<PolicyResource> getPoliciesEndpoint(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            PolicySearchOptions searchOptions
    ) {
        return getPolicies(pageable, searchOptions);
    }

    public abstract Page<PolicyResource> getPolicies(Pageable pageable, PolicySearchOptions searchOptions);


    // ===============================================================================
    // GET /policies/{id}
    // ===============================================================================

    @Operation(
            summary = "Get a Policy",
            description = "Get the active Policy (i.e., the last version) identified by the given Root ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested Policy",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    schema = @Schema(implementation = PolicyResource.class)),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    schema = @Schema(implementation = PolicyResource.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PolicyResource.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Policy not found",
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
    public PolicyResource getPolicyEndpoint(
            @Parameter(description = "The root ID of the Policy", required = true)
            @PathVariable(value = "id") Long id
    ) {
        return getPolicy(id);
    }

    public abstract PolicyResource getPolicy(Long id);


    // ===============================================================================
    // GET /policies/versions/{versionId}
    // ===============================================================================

    @Operation(
            summary = "Get a Policy version",
            description = "Get the Policy version identified by the given ID, whether it is active or not"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested Policy",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    schema = @Schema(implementation = PolicyResource.class)),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    schema = @Schema(implementation = PolicyResource.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = PolicyResource.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Policy not found",
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
            value = "/versions/{versionId}",
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public PolicyResource getPolicyVersionEndpoint(
            @Parameter(description = "The ID of the Policy")
            @PathVariable(value = "versionId") Long versionId
    ) {
        return getPolicyVersion(versionId);
    }

    public abstract PolicyResource getPolicyVersion(Long versionId);


    // ===============================================================================
    // POST /policies
    // ===============================================================================

    @Operation(
            summary = "Create a Policy",
            description = "Create a single Policy"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Policy created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PolicyResource.class),
                            examples = {@ExampleObject(name = "engine1", value = EXAMPLE_POLICY)}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40001 - Policy is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42201 - Policy is invalid"
                            + "\r\n - Error Code 42202 - Policy already exists",
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
    public PolicyResource createPolicyEndpoint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A PolicyEngine JSON object",
                    content = @Content(examples = {@ExampleObject(
                            name = "policy-creation-example",
                            description = "Example of a Policy OPA",
                            value = EXAMPLE_POLICY_CREATE
                    )}))
            @RequestBody PolicyResource policy
    ) {
        return createPolicy(policy);
    }

    public abstract PolicyResource createPolicy(PolicyResource policy);


    // ===============================================================================
    // PUT /policies/{id}
    // ===============================================================================

    @Operation(
            summary = "Update a Policy",
            description = "Update the given Policy"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Policy updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PolicyResource.class),
                            examples = {@ExampleObject(name = "engine1", value = EXAMPLE_POLICY)}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40001 - Policy is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Policy not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42201 - Policy is invalid"
                            + "\r\n - Error Code 42202 - Policy already exists",
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
    public PolicyResource modifyPolicyEndpoint(
            @Parameter(description = "The ID of the Policy to update")
            @PathVariable(value = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A PolicyEngine JSON object",
                    content = @Content(examples = {@ExampleObject(
                            name = "policy-update-example",
                            description = "Example of a Policy OPA",
                            value = EXAMPLE_POLICY_UPDATE
                    )}))
            @RequestBody PolicyResource policy
    ) {
        return modifyPolicy(id, policy);
    }

    public abstract PolicyResource modifyPolicy(Long id, PolicyResource policy);


    // ===============================================================================
    // DELETE /policies/{id}
    // ===============================================================================

    @Operation(
            summary = "Delete a Policy",
            description = "Delete a Policy given its ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested Policy was delete successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Policy not found",
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
    @DeleteMapping(value = "/{id}")
    public void deletePolicyEndpoint(
            @Parameter(description = "ID of the Policy to Delete")
            @PathVariable(value = "id") Long id
    ) {
        deletePolicy(id);
    }

    public abstract void deletePolicy(Long id);

}
