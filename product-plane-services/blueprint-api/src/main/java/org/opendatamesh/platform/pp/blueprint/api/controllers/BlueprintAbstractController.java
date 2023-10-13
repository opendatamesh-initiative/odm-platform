package org.opendatamesh.platform.pp.blueprint.api.controllers;

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
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
import org.opendatamesh.platform.pp.blueprint.api.resources.ConfigResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
        value = "/blueprints"
)
@Validated
@Tag(
        name = "Blueprints",
        description = "Endpoints associated to blueprints collection"
)
public abstract class BlueprintAbstractController {

    // TODO: add SEARCH endpoint

    private static final String EXAMPLE_BLUEPRINT = "{\n" + //
            "   \"name\": \"blueprint1\",\n" + //
            "   \"version\": \"1.0.0\",\n" + //
            "   \"displayName\": \"first blueprint\",\n" + //
            "   \"description\": \"description of blueprint\",\n" + //
            "   \"repositoryProvider\": \"AZURE_DEVOPS\",\n" + //
            "   \"repositoryBaseUrl\": \"https://github.com/opendatamesh-initiative\",\n" + //
            "   \"blueprintRepo\": \"/blueprint1\",\n" + //
            "   \"targetRepo\": \"/\"\n" + //
            "}";
    private static final String EXAMPLE_BLUEPRINT_UPDATE = "{\n" + //
            "   \"id\": 1,\n" + //
            "   \"name\": \"blueprint1 - updated\",\n" + //
            "   \"version\": \"1.0.1\",\n" + //
            "   \"displayName\": \"first blueprint\",\n" + //
            "   \"description\": \"description of blueprint\",\n" + //
            "   \"repositoryProvider\": \"AZURE_DEVOPS\",\n" + //
            "   \"repositoryBaseUrl\": \"https://github.com/opendatamesh-initiative\",\n" + //
            "   \"blueprintRepo\": \"/blueprints1\",\n" + //
            "   \"targetRepo\": \"/project1.0.1\"\n" + //
            "}";

    private static final String EXAMPLE_CONFIG = "{\n" + //
            "   \"parameter1\": \"value of parameter 2\",\n" + //
            "   \"parameter2\": \"value of parameter 2\",\n" + //
            "   \"parameter3\": \"value of parameter 3\"\n" + //
            "}";

    // @see https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations#arrayschema
    
    
    // ===============================================================================
    // GET /blueprints
    // ===============================================================================

    @Operation(
            summary = "Get all blueprints",
            description = "Get all the registered blueprints"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All the blueprints",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    array = @ArraySchema(schema = @Schema(implementation = BlueprintResource.class))),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    array = @ArraySchema(schema = @Schema(implementation = BlueprintResource.class))),
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = BlueprintResource.class)))
                    }
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service"
                            + "\r\n - Error Code 50002 - Error in the backend descriptor processor",
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
    public List<BlueprintResource> readBlueprintsEndpoint() {
        return readBlueprints();
    }

    public abstract List<BlueprintResource> readBlueprints();
    
    
    // ===============================================================================
    // GET /blueprints/{id}
    // ===============================================================================

    @Operation(
            summary = "Get a specific blueprint",
            description = "Get the blueprint identified by 'id'"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested blueprint",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    schema = @Schema(implementation = BlueprintResource.class)),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    schema = @Schema(implementation = BlueprintResource.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = BlueprintResource.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Blueprint not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service"
                            + "\r\n - Error Code 50002 - Error in the backend descriptor processor",
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
    public BlueprintResource readBlueprintEndpoint(
            @Parameter(description = "Identifier of the blueprint", required = true)
            @Valid @PathVariable(value = "id") Long id
    ) {
        return readBlueprint(id);
    }

    public abstract BlueprintResource readBlueprint(Long id);


    // ===============================================================================
    // POST /blueprints
    // ===============================================================================

    @Operation(
            summary = "Create a new blueprint",
            description = "Register the provided blueprint"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Blueprint created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BlueprintResource.class),
                            examples = {
                                    @ExampleObject(name = "one", value = EXAMPLE_BLUEPRINT)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40001 - Blueprint is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42201 - Blueprint is invalid"
                            + "\r\n - Error Code 42202 - Blueprint already exists",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service"
                            + "\r\n - Error Code 50002 - Error in the backend descriptor processor",
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
    public BlueprintResource createBlueprintEndpoint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A blueprint object",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "blueprint",
                                            description = "description of blueprint example",
                                            value = EXAMPLE_BLUEPRINT
                                    )
                            }
                    )
            )
            @RequestBody(required = false) BlueprintResource blueprint
    ) {
        return createBlueprint(blueprint);
    }

    public abstract BlueprintResource createBlueprint(BlueprintResource blueprint);


    // ===============================================================================
    // PUT /blueprints/{id}
    // ===============================================================================

    @Operation(
            summary = "Update a specific blueprint",
            description = "Update the provided blueprint"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Blueprint correctly updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = BlueprintResource.class),
                            examples = {
                                    @ExampleObject(name = "blueprintUpdate", value = EXAMPLE_BLUEPRINT_UPDATE)
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40001 - Blueprint is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Blueprint not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42201 - Blueprint is invalid",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service"
                            + "\r\n - Error Code 50002 - Error in the backend descriptor processor",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    @PutMapping(
            value = "/{id}",
            consumes = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"},
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public BlueprintResource updateBlueprintEndpoint(
            @Parameter(description = "Identifier of the blueprint to update", required = true)
            @Valid @PathVariable(value = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A blueprint object",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "blueprintUpdate",
                                            description = "description of blueprint update example",
                                            value = EXAMPLE_BLUEPRINT_UPDATE
                                    )
                            }
                    )
            )
            @RequestBody(required=false) BlueprintResource blueprint
    ) {
        return updateBlueprint(id, blueprint);
    }

    public abstract BlueprintResource updateBlueprint(Long id, BlueprintResource blueprint);


    // ===============================================================================
    // DELETE /blueprints/{id}
    // ===============================================================================

    @Operation(
            summary = "Delete a specific blueprint",
            description = "Delete the blueprint identified by 'id'"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested blueprint was delete successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Blueprint not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service"
                            + "\r\n - Error Code 50002 - Error in the backend descriptor processor",
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
    public void deleteBlueprintEndpoint(
            @Parameter(description = "Identifier of the blueprint to delete")
            @Valid @PathVariable(value = "id") Long id
    ) {
        deleteBlueprint(id);
    }

    public abstract void deleteBlueprint(Long id);


    // ===============================================================================
    // POST - INIT /blueprints/{id}/init
    // ===============================================================================

    @Operation(
            summary = "Init a specific blueprint",
            description = "Create a project templating the blueprint identified by 'id' with the given parameters (i.e., 'config' object)"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The project from the requested blueprint was created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40002 - Config object is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Blueprint not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service"
                            + "\r\n - Error Code 50002 - Error in the backend descriptor processor",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    @PostMapping(
            value = "/{id}/init",
            consumes = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"}
    )
    public void initBlueprintEndpoint(
            @Parameter(description = "Identifier of the blueprint to init")
            @Valid @PathVariable(value = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A config object",
                    content = @Content(
                            examples = {
                                    @ExampleObject(
                                            name = "configExample",
                                            description = "description of config example",
                                            value = EXAMPLE_CONFIG
                                    )
                            }
                    )
            )
            @RequestBody(required=false) ConfigResource config
    ) {
        initBlueprint(id, config);
    }

    public abstract void initBlueprint(Long blueprintId, ConfigResource configResource);

}
