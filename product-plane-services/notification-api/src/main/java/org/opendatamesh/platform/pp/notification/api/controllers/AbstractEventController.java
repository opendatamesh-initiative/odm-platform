package org.opendatamesh.platform.pp.notification.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(
        value = "/events"
)
@Validated
@Tag(
        name = "Events",
        description = "Endpoints associated to Events info"
)
public abstract class AbstractEventController implements EventController {

    // ===============================================================================
    // GET /events/{eventId}
    // ===============================================================================

    @Operation(
            summary = "Get an Event",
            description = "Get the Event identified by the given ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested Event",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    schema = @Schema(implementation = EventResource.class)),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    schema = @Schema(implementation = EventResource.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EventResource.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40402 - Event not found",
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
            value = "/{eventId}",
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public EventResource readOneEventEndpoint(
            @Parameter(description = "ID of the desired Event", required = true)
            @Valid @PathVariable(value = "eventId") Long eventId
    ) {
        return readOneEvent(eventId);
    }

    public abstract EventResource readOneEvent(
            Long eventId
    );


    // ===============================================================================
    // GET /events
    // ===============================================================================

    @Operation(
            summary = "Get all Events",
            description = "Get all the registered Events paginated"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All the Events",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    array = @ArraySchema(schema = @Schema(implementation = EventResource.class))),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    array = @ArraySchema(schema = @Schema(implementation = EventResource.class))),
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = EventResource.class)))
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
    public Page<EventResource> searchEventsEndpoint(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            EventSearchOptions searchOptions
    ) {
        return searchEvents(pageable, searchOptions);
    }

    public abstract Page<EventResource> searchEvents(
            Pageable pageable, EventSearchOptions searchOptions
    );

}
