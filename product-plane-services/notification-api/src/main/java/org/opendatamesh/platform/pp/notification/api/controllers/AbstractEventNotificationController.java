package org.opendatamesh.platform.pp.notification.api.controllers;

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
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/notifications")
@Validated
@Tag(
        name = "Notifications",
        description = "Endpoints associated to Notifications lifecycle"
)
public abstract class AbstractEventNotificationController implements EventNotificationController {

    // TODO: add examples

    // ===============================================================================
    // Resource examples
    // ===============================================================================

    private static final String EXAMPLE_NOTIFICATION = "{\n" + //
            "}";

    private static final String EXAMPLE_NOTIFICATION_UPDATE = "{\n" + //
            "}";


    // ===============================================================================
    // PUT /notifications/{notificationId}
    // ===============================================================================

    @Operation(
            summary = "Update an Notification",
            description = "Update a specific Notification"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = EventNotificationResource.class),
                            examples = {@ExampleObject(name = "engine1", value = EXAMPLE_NOTIFICATION)}
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40003 - Notification is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40403 - Notification not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42203 - Notification is invalid",
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
    public EventNotificationResource updateEventNotificationEndpoint(
            @Parameter(description = "ID of the Notification to update", required = true)
            @PathVariable(value = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "An Notification JSON object",
                    content = @Content(examples = {@ExampleObject(
                            name = "notification-update-example",
                            description = "Example of a Notification for the update API",
                            value = EXAMPLE_NOTIFICATION_UPDATE
                    )}))
            @RequestBody(required = false) EventNotificationResource notification
    ) {
        return updateEventNotification(id, notification);
    }

    public abstract EventNotificationResource updateEventNotification(Long id, EventNotificationResource eventNotificationResource);
    

    // ===============================================================================
    // GET /notifications/{notificationId}
    // ===============================================================================

    @Operation(
            summary = "Get a Notification",
            description = "Get the Notification identified by the given ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested Notification",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    schema = @Schema(implementation = EventNotificationResource.class)),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    schema = @Schema(implementation = EventNotificationResource.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = EventNotificationResource.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40403 - Notification not found",
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
            value = "/{notificationId}",
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public EventNotificationResource readOneEventNotificationEndpoint(
            @Parameter(description = "ID of the desired Notification", required = true)
            @Valid @PathVariable(value = "notificationId") Long notificationId
    ) {
        return readOneEventNotification(notificationId);
    }

    public abstract EventNotificationResource readOneEventNotification(
        Long notificationId
    );


    // ===============================================================================
    // GET /notifications
    // ===============================================================================

    @Operation(
            summary = "Get all Notifications",
            description = "Get all the registered Notifications paginated and, eventually, filtered"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All the Notifications",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    array = @ArraySchema(schema = @Schema(implementation = EventNotificationResource.class))),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    array = @ArraySchema(schema = @Schema(implementation = EventNotificationResource.class))),
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = EventNotificationResource.class)))
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
    public Page<EventNotificationResource> searchEventNotificationsEndpoint(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            EventNotificationSearchOptions searchOptions
    ) {
        return searchEventNotifications(pageable, searchOptions);
    }

    public abstract Page<EventNotificationResource> searchEventNotifications(
        Pageable pageable, EventNotificationSearchOptions searchOptions
    );
    
}
