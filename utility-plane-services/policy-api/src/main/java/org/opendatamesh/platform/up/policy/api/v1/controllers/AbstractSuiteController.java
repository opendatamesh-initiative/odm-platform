package org.opendatamesh.platform.up.policy.api.v1.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.up.policy.api.v1.enums.PatchModes;
import org.opendatamesh.platform.up.policy.api.v1.resources.ErrorResource;
import org.opendatamesh.platform.up.policy.api.v1.resources.SuiteResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(
        value = "/suites",
        produces = { "application/json" }
)
@Validated
@Tag(
        name = "Suite API",
        description = "CRUD API for Suite entity"
)
public abstract class AbstractSuiteController {

    @GetMapping
    @Operation(
            summary = "Get all suites",
            description = "Fetch all registered suites",
            tags = { "Suite API" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All registered suites",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuiteResource.class)
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
    public ResponseEntity getSuites(){
        return readSuites();
    }

    public abstract ResponseEntity readSuites();

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a suite",
            description = "Fetch a specific registered suite given its ID",
            tags = { "Suite API" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The registered suite",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuiteResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error code 40402 - Suite not found",
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
    public ResponseEntity getSuitesByID(
            @Parameter(description = "Identifier of the suite")
            @Valid @PathVariable String id
    ){
        return readOneSuite(id);
    }

    public abstract ResponseEntity readOneSuite(String id);

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create a new suite",
            description = "Create and register a new suite (i.e., collection of OPA policies)",
            tags = { "Suite API" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Suite created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuiteResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error code 40005 - Suite already exists"
                            + "\r\n - Error code 40006 - OPA Bad Request",
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
    public ResponseEntity postSuite(
            @Parameter(description = "JSON description of the suite object")
            @Valid @RequestBody SuiteResource suite
    ){
        return createSuite(suite);
    }

    public abstract ResponseEntity createSuite(SuiteResource suite);

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a suite",
            description = "Delete a registered suite given its ID",
            tags = { "Suite API" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Suite deleted",
                    content = @Content(
                            mediaType = "plain/text",
                            schema = @Schema(implementation = String.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error code 40402 - Suite not found",
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
    public ResponseEntity deleteSuiteByID(
            @Parameter(description = "Identifier of the suite")
            @Valid @PathVariable String id
    ){
        return deleteSuite(id);
    }

    public abstract ResponseEntity deleteSuite(String id);

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update a suite",
            description = "Add or remove a policy, through its ID, from a registered suite",
            tags = { "Suite API" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Suite patched",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuiteResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error code 40402 - Suite not found",
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
    public ResponseEntity patchSuiteByID(
            @Parameter(description = "Identifier of the suite")
            @Valid @PathVariable String id,
            @Parameter(description = "Patch mode - whether it ADD a policy or REMOVE a policy from a registered suite - could only be {\"ADD\", \"REMOVE\"}")
            @Valid @RequestParam PatchModes mode,
            @Parameter(description = "Identifier of the policy to add/remove from the suite")
            @Valid @RequestParam String policyId
    ){
        return updateSuite(id, mode, policyId);
    }

    public abstract ResponseEntity updateSuite(String suiteId, PatchModes mode, String policyId);

}
