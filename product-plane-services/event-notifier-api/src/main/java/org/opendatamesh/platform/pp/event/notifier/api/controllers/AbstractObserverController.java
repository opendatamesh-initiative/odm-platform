package org.opendatamesh.platform.pp.event.notifier.api.controllers;

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
import org.opendatamesh.platform.pp.event.notifier.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ObserverSearchOptions;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/observers"
)
@Validated
@Tag(
        name = "Observers",
        description = "Endpoints associated to Observers lifecycle"
)
public abstract class AbstractObserverController implements ObserverController {

    // ===============================================================================
    // Resource examples
    // ===============================================================================

    private static final String EXAMPLE_OBSERVER = "{\n" + //
            "   \"id\": 1,\n" + //
            "   \"name\": \"custom-observer\",\n" + //
            "   \"displayName\": \"Custom Observer\",\n" + //
            "   \"observerServerAddress\": \"http://localhost:9009/api/v1/up/notification-service\",\n" + //
            "   \"createdAt\": \"2024-03-21T12:04:11.000+00:00\",\n" + //
            "   \"updatedAt\": \"2024-03-21T12:14:29.000+00:00\"\n" + //
            "}";

    private static final String EXAMPLE_OBSERVER_CREATE = "{\n" + //
            "   \"name\": \"custom-observer\",\n" + //
            "   \"displayName\": \"Custom Observer\",\n" + //
            "   \"observerServerAddress\": \"http://localhost:9009/api/v1/up/notification-service\",\n" + //
            "}";

    private static final String EXAMPLE_OBSERVER_UPDATE = "{\n" + //
            "   \"id\": 1,\n" + //
            "   \"name\": \"custom-observer\",\n" + //
            "   \"displayName\": \"Custom Observer Updated\",\n" + //
            "   \"observerServerAddress\": \"http://localhost:9009/api/v1/up/notifier\",\n" + //
            "   \"createdAt\": \"2024-03-21T12:04:11.000+00:00\"\n" + //
            "}";


    // ===============================================================================
    // POST /observers
    // ===============================================================================

    @Operation(
            summary = "Add an Observer",
            description = "Add a single listening Notification Adapter (i.e., an Observer)"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Observer created",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ObserverResource.class),
                            examples = {@ExampleObject(name = "engine1", value = EXAMPLE_OBSERVER)}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40001 - Observer is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42201 - Observer is invalid"
                            + "\r\n - Error Code 42202 - Observer already exists",
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
    public ObserverResource addObserverEndpoint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "An Observer JSON object",
                    content = @Content(examples = {@ExampleObject(
                            name = "observer-creation-example",
                            description = "Example of a listening Notification Adapter",
                            value = EXAMPLE_OBSERVER_CREATE
                    )}))
            @RequestBody(required = false) ObserverResource observer
    ) {
        return addObserver(observer);
    }

    public abstract ObserverResource addObserver(ObserverResource observerResource);


    // ===============================================================================
    // PUT /observer
    // ===============================================================================

    @Operation(
            summary = "Update an Observer",
            description = "Update a specific listening Notification Adapter"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Observer updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ObserverResource.class),
                            examples = {@ExampleObject(name = "engine1", value = EXAMPLE_OBSERVER)}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40001 - Observer is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Observer not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42201 - Observer is invalid"
                            + "\r\n - Error Code 42202 - Observer already exists",
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
    public ObserverResource updateObserverEndpoint(
            @Parameter(description = "ID of the Observer to update", required = true)
            @PathVariable(value = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "An Observer JSON object",
                    content = @Content(examples = {@ExampleObject(
                            name = "observer-creation-example",
                            description = "Example of a listening Notification Adapter",
                            value = EXAMPLE_OBSERVER_UPDATE
                    )}))
            @RequestBody(required = false) ObserverResource observer
    ) {
        return updateObserver(id, observer);
    }

    public abstract ObserverResource updateObserver(Long id, ObserverResource observerResource);


    // ===============================================================================
    // GET /observers
    // ===============================================================================

    @Operation(
            summary = "Get all Policies",
            description = "Get all the registered Policy paginated"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All the Observers",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    array = @ArraySchema(schema = @Schema(implementation = ObserverResource.class))),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    array = @ArraySchema(schema = @Schema(implementation = ObserverResource.class))),
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ObserverResource.class)))
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
    public Page<ObserverResource> getObserversEndpoint(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            ObserverSearchOptions searchOptions
    ) {
        return getObservers(pageable, searchOptions);
    }

    public abstract Page<ObserverResource> getObservers(Pageable pageable, ObserverSearchOptions searchOptions);


    // ===============================================================================
    // GET /observers/{id}
    // ===============================================================================

    @Operation(
            summary = "Get an Observer",
            description = "Get the Observer identified by the given ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested Observer",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    schema = @Schema(implementation = ObserverResource.class)),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    schema = @Schema(implementation = ObserverResource.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = ObserverResource.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Observer not found",
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
    public ObserverResource getObserverEndpoint(
            @Parameter(description = "ID of the desired Observer", required = true)
            @PathVariable(value = "id") Long id
    ) {
        return getObserver(id);
    }

    public abstract ObserverResource getObserver(Long id);


    // ===============================================================================
    // DELETE /observers/{id}
    // ===============================================================================

    @Operation(
            summary = "Remove an Observer",
            description = "Remove a single listening Notification Adapter given its ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested Observer was delete successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Observer not found",
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
    public void removeObserverEndpoint(
            @Parameter(description = "ID of the Observer to delete")
            @PathVariable(value = "id") Long id
    ) {
        removeObserver(id);
    }

    public abstract void removeObserver(Long id);

}
