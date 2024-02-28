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
        name = "Policy Engines",
        description = "Endpoints associated to Policy Engines"
)
public abstract class AbstractPolicyEngineController {

    private static final String EXAMPLE_POLICY_ENGINE_CREATION = "{\n" + //
            "   \"name\": \"opa-policy-checker\",\n" + //
            "   \"displayName\": \"OPA Policy Checker\",\n" + //
            "   \"adapterUrl\": \"http://localhost:9001/api/v1/up/policy-engine-adapter\"\n" + //
            "}";

    @GetMapping
    public abstract Page<PolicyEngineResource> getPolicyEngines(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            PolicyEngineSearchOptions searchOptions
    );

    @GetMapping(value = "/{id}")
    public abstract PolicyEngineResource getPolicyEngine(
            @Parameter(description = "", required = true)
            @PathVariable(value = "id") Long id
    );


    @Operation(
            summary = "Create a Policy Engine",
            description = "Create a single Policy Engine"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Blueprint created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PolicyEngineResource.class),
                            examples = {
                                    @ExampleObject(name = "engine1", value = EXAMPLE_POLICY_ENGINE_CREATION)
                            }
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
            @Parameter(description = "")
            @RequestBody PolicyEngineResource policyEngine
    ) {
        return createPolicyEngine(policyEngine);
    }

    public abstract PolicyEngineResource createPolicyEngine(PolicyEngineResource policyEngineResource);

    @PutMapping(value = "/{id}")
    public abstract PolicyEngineResource modifyPolicyEngine(
            @Parameter(description = "")
            @PathVariable(value = "id") Long id,
            @Parameter(description = "")
            @RequestBody PolicyEngineResource policyEngine
    );

    @DeleteMapping(value = "/{id}")
    public abstract PolicyEngineResource deletePolicyEngine(
            @Parameter(description = "")
            @PathVariable(value = "id") Long id
    );

}
