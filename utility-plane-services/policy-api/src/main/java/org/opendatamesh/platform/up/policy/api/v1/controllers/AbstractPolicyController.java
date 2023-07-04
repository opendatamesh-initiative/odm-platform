package org.opendatamesh.platform.up.policy.api.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.up.policy.api.v1.resources.ErrorResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.PolicyResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(
        value = "/policies",
        produces = { "application/json" }
)
@Validated
@Tag(
        name = "Policy API",
        description = "CRUD API for Policy entity"
)
public abstract class AbstractPolicyController {

    @GetMapping
    @Operation(
            summary = "Get all policies",
            description = "Fetch all registered policies",
            tags = { "Policy API" }
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All registered policies",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PolicyResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error code 50000 - Generic internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResource.class)
                    )
            )
    })
    public ResponseEntity getPolicies(){
        return readPolicies();
    }

    public abstract ResponseEntity readPolicies();

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a policy",
            description = "Fetch a specific registered policy given its ID",
            tags = { "Policy API" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The registered policy",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PolicyResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error code 40401 - Policy not found",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error code 50000 - Generic internal server error",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorResource.class)
                    )
            )
    })
    public ResponseEntity getPolicyByID(
            @Parameter(description = "Identifier of the policy")
            @Valid @PathVariable String id
    ){
        return readOnePolicy(id);
    }

    public abstract ResponseEntity readOnePolicy(String id);

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create a new policy",
            description = "Create and register a new OPA policy",
            tags = { "Policy API" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Policy created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PolicyResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error code 40001 - Policy already exists"
                            + "\r\n - Error code 40006 - OPA Server bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResource.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error code 50000 - Generic internal server error"
                            + "\r\n - Error code 50001 - OPA Server internal server error"
                            + "\r\n - Error code 50002 - OPA Server not reachable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResource.class))
            )
    })
    public ResponseEntity postPolicy(
            @Parameter(description = "JSON description of the policy object")
            @Valid @RequestBody PolicyResource policies
    ){
        return createPolicy(policies);
    }

    public abstract ResponseEntity createPolicy(PolicyResource policies);

    @PutMapping(
            path = "/{id}",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @Operation(
            summary = "Update a policy",
            description = "Update a registered OPA policy",
            tags = { "Policy API" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Policy updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PolicyResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error code 40004 - ID conflict"
                            + "\r\n - Error code 40006 - OPA Server bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResource.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error code 40401 - Policy not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResource.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error code 50000 - Generic internal server error"
                            + "\r\n - Error code 50001 - OPA Server internal server error"
                            + "\r\n - Error code 50002 - OPA Server not reachable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResource.class))
            )
    })
    public ResponseEntity putPolicyByID(
            @Parameter(description = "Identifier of the policy")
            @Valid @PathVariable String id,
            @Parameter(description = "JSON description of the policy object to update")
            @Valid @RequestBody PolicyResource policies
    ){
        return updatePolicy(id, policies);
    }

    public abstract ResponseEntity updatePolicy(
            String id,
            PolicyResource policies
    );

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Delete a policy",
            description = "Delete a registered OPA policy given its ID",
            tags = { "Policy API" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error code 40006 - OPA Server bad request",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResource.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error code 40401 - Policy not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResource.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error code 50000 - Generic internal server error"
                            + "\r\n - Error code 50001 - OPA Server internal server error"
                            + "\r\n - Error code 50002 - OPA Server not reachable",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResource.class))
            )
    })
    public void deletePolicyByID(
            @Parameter(description = "Identifier of the policy")
            @Valid @PathVariable String id
    ){
        deletePolicy(id);
    }

    public abstract void deletePolicy(String id);

}

