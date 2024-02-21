package org.opendatamesh.platform.pp.params.api.controllers;

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
import org.opendatamesh.platform.pp.params.api.resources.ParamResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
        value = "/params"
)
@Validated
@Tag(
        name = "Params",
        description = "Endpoints associated to parameters collection"
)
public abstract class AbstractParamController {

    private static final String EXAMPLE_ONE = "{\n" + //
            "    \"paramName\": \"spring.port\",\n" + //
            "    \"paramValue\": \"8001\",\n" + //
            "    \"displayName\": \"Port\",\n" + //
            "    \"description\": \"Port of Spring application\",\n" + //
            "    \"secret\": \"false\"\n" + //
            "}";

    private static final String EXAMPLE_TWO = "{\n" + //
            "    \"id\": \"1\",\n" + //
            "    \"paramName\": \"spring.port\",\n" + //
            "    \"paramValue\": \"8001\",\n" + //
            "    \"displayName\": \"Port\",\n" + //
            "    \"description\": \"Port of Spring application\",\n" + //
            "    \"secret\": \"false\",\n" + //
            "    \"createdAt\": \"2024-01-01 01:15:36\",\n" + //
            "    \"updatedAt\": null\n" + //
            "}";

    // ===============================================================================
    // POST /params
    // ===============================================================================

    @Operation(
            summary = "Create a new parameter",
            description = "Create a new global parameter given name and value"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Parameter created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ParamResource.class),
                            examples = {
                                    @ExampleObject(name = "example-two", value = EXAMPLE_TWO)}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40001 - Parameter is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42201 - Parameter is invalid"
                            + "\r\n - Error Code 42202 - Parameter already exists",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service"
                            + "\r\n - Error Code 50003 - Error in in the encryption service",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    @PostMapping(
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
    public ParamResource createParamEndpoint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A Param object",
                    required = true,
                    content = @Content(
                            examples = {
                                    @ExampleObject(name = "example-one", description = "example of a parameter", value = EXAMPLE_ONE)}
                    )
            )
            @RequestBody(required = false) ParamResource param
    ) {
        return createParam(param);
    }

    public abstract ParamResource createParam(ParamResource param);


    // ===============================================================================
    // PUT /params/{id}
    // ===============================================================================

    @Operation(
            summary = "Update a parameter",
            description = "Update the provided global parameter"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Parameter updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ParamResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40001 - Parameter is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Parameter not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42201 - Parameter is invalid",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50001 - Error in the backend database"
                            + "\r\n - Error Code 50002 - Error in in the backend service"
                            + "\r\n - Error Code 50003 - Error in in the encryption service",
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
    public ParamResource updateParamEndpoint(
            @Parameter(description = "Identifier of the parameter")
            @Valid @PathVariable(value = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A Param object",
                    required = true,
                    content = @Content(examples = {@ExampleObject(
                            name = "example-one",
                            description = "example of a parameter",
                            value = EXAMPLE_ONE
                    )}))
            @RequestBody(required = false) ParamResource param
    ) {
        return updateParam(id, param);
    }

    public abstract ParamResource updateParam(Long id, ParamResource param);


    // ===============================================================================
    // GET /params
    // ===============================================================================

    @Operation(
            summary = "Get all parameter",
            description = "Get all the registered parameters"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All registered parameters",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    array = @ArraySchema(schema = @Schema(implementation = ParamResource.class))),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    array = @ArraySchema(schema = @Schema(implementation = ParamResource.class))),
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ParamResource.class)))
                    }
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service"
                            + "\r\n - Error Code 50003 - Error in in the encryption service",
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
    public List<ParamResource> getParamsEndpoint(
            @RequestHeader(value = "client-UUID", required = false) String clientUUID
    ) {
        return getParams(clientUUID);
    }

    public abstract List<ParamResource> getParams(String clientUUID);


    // ===============================================================================
    // GET /params/filter
    // ===============================================================================
    @Operation(
            summary = "Get the specified parameter",
            description = "Get the parameter identified by the input `name`"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested parameter",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    schema = @Schema(implementation = ParamResource.class)),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    schema = @Schema(implementation = ParamResource.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ParamResource.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Parameter not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service"
                            + "\r\n - Error Code 50003 - Error in in the encryption service",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    @GetMapping(
            value = "/filter",
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public ParamResource getParamByNameEndpoint(
            @RequestHeader(value = "client-UUID", required = false) String clientUUID,
            @Parameter(description="Name of the desired parameter")
            @RequestParam(name = "name") String name
    ) {
        return getParamByName(name, clientUUID);
    }

    public abstract ParamResource getParamByName(String name, String clientUUID);


    // ===============================================================================
    // GET /params/{id}
    // ===============================================================================

    @Operation(
            summary = "Get the specified parameter",
            description = "Get the parameter identified by the input `id`"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested parameter",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    schema = @Schema(implementation = ParamResource.class)),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    schema = @Schema(implementation = ParamResource.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ParamResource.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Parameter not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in the backend database"
                            + "\r\n - Error Code 50001 - Error in in the backend service"
                            + "\r\n - Error Code 50003 - Error in in the encryption service",
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
    public ParamResource getParamEndpoint(
            @RequestHeader(value = "client-UUID", required = false) String clientUUID,
            @Parameter(description = "Identifier of the parameter")
            @PathVariable(value = "id") Long id
    ) {
        return getParam(id, clientUUID);
    }

    public abstract ParamResource getParam(Long id, String clientUUID);


    // ===============================================================================
    // DELETE /params/{id}
    // ===============================================================================

    @Operation(
            summary = "Delete a specific parameter",
            description = "Delete the parameter identified by 'id'"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested parameter was delete successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Parameter not found",
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
    public void deleteParamEndpoint(
            @Parameter(description = "Identifier of the parameter")
            @PathVariable Long id
    ) {
        deleteParam(id);
    }

    public abstract void deleteParam(Long id);

}
