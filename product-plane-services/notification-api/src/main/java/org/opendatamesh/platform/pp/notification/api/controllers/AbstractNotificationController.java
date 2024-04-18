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
import org.opendatamesh.platform.pp.notification.api.resources.NotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.NotificationSearchOptions;
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
public abstract class AbstractNotificationController implements NotificationController {

    // TODO: add examples

    // ===============================================================================
    // Resource examples
    // ===============================================================================

    private static final String EXAMPLE_NOTIFICATION = "{\n" + //
            "}";

    private static final String EXAMPLE_NOTIFICATION_CREATE = "{\n" + //
            "}";

    private static final String EXAMPLE_NOTIFICATION_UPDATE = "{\n" + //
            "}";

    // ===============================================================================
    // POST /notifications
    // ===============================================================================

    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create the notification",
        description = "Create the notification and start the handling process. The `id` of the created notification can be used to query asynchronously its handling status" 
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Notification created",
                content = @Content(
                        mediaType = "application/json",
                        schema = @Schema(implementation = NotificationResource.class),
                        examples = {@ExampleObject(name = "notification", value = EXAMPLE_NOTIFICATION)}
                )
        ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40003 - Notification is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42203 - Notification is invalid"
                            + "\r\n - Error Code 42204 - Notification already exists",
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
    public NotificationResource createNotificationEndpoint(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "A Notification JSON object",
                content = @Content(examples = {@ExampleObject(
                        name = "notification-creation-example",
                        description = "Example of a Notification for the create API",
                        value = EXAMPLE_NOTIFICATION_CREATE
                )}))
        @Valid @RequestBody(required = false) NotificationResource notificationRes
    ) {
        return createNotification(notificationRes);
    }

    public abstract NotificationResource createNotification(NotificationResource notificationRes);


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
                            schema = @Schema(implementation = NotificationResource.class),
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
                            + "\r\n - Error Code 40402 - Notification not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42203 - Notification is invalid"
                            + "\r\n - Error Code 42204 - Notification already exists",
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
    public NotificationResource updateNotificationEndpoint(
            @Parameter(description = "ID of the Notification to update", required = true)
            @PathVariable(value = "id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "An Notification JSON object",
                    content = @Content(examples = {@ExampleObject(
                            name = "notification-update-example",
                            description = "Example of a Notification for the update API",
                            value = EXAMPLE_NOTIFICATION_UPDATE
                    )}))
            @RequestBody(required = false) NotificationResource notification
    ) {
        return updateNotification(id, notification);
    }

    public abstract NotificationResource updateNotification(Long id, NotificationResource notificationResource);
    

    // ===============================================================================
    // GET /notifications/{notificationId}
    // ===============================================================================

    @Operation(
            summary = "Get an Notification",
            description = "Get the Notification identified by the given ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested Notification",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    schema = @Schema(implementation = NotificationResource.class)),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    schema = @Schema(implementation = NotificationResource.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = NotificationResource.class))
                    }
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40402 - Notification not found",
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
    public NotificationResource readOneNotificationEndpoint(
            @Parameter(description = "ID of the desired Notification", required = true)
            @Valid @PathVariable(value = "notificationId") Long notificationId
    ) {
        return readOneNotification(notificationId);
    }

    public abstract NotificationResource readOneNotification(
        Long notificationId
    );


    // ===============================================================================
    // GET /notifications
    // ===============================================================================

    @Operation(
            summary = "Get all Notifications",
            description = "Get all the registered Notifications paginated"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All the Notifications",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    array = @ArraySchema(schema = @Schema(implementation = NotificationResource.class))),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    array = @ArraySchema(schema = @Schema(implementation = NotificationResource.class))),
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = NotificationResource.class)))
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
    public Page<NotificationResource> searchNotificationsEndpoint(
            @PageableDefault(size = 20, page = 0)
            Pageable pageable,
            NotificationSearchOptions searchOptions
    ) {
        return searchNotifications(pageable, searchOptions);
    }

    public abstract Page<NotificationResource> searchNotifications(
        Pageable pageable, NotificationSearchOptions searchOptions
    );


    // ===============================================================================
    // DELETE /notifications/{notificationId}
    // ===============================================================================

    @Operation(
            summary = "Delete an Notification",
            description = "Delete a single Notification given its ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested Notification was deleted successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40402 - Notification not found",
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
    @DeleteMapping("/{notificationId}")
    public void deleteNotificationEndpoint(
            @Parameter(description = "ID of the Notification to delete", required = true)
            @Valid @PathVariable(value = "notificationId") Long notificationId
    )  {
        deleteNotification(notificationId);
    } 

    public abstract void deleteNotification(Long notificationId);
    
}
