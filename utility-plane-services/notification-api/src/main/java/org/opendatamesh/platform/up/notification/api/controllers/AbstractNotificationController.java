package org.opendatamesh.platform.up.notification.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/notifications")
@Validated
public abstract class AbstractNotificationController {

    @PostMapping(
        consumes = { "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", "application/json"}
    )
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
                schema = @Schema(implementation = NotificationResource.class)
            )
        )
    })
    public NotificationResource createNotificationEndpoint(
        @Parameter( 
            description = "A notification object", 
            required = true)
        @Valid @RequestBody NotificationResource notificationRes
    ) {
        return createNotification(notificationRes);
    }

    public abstract NotificationResource createNotification(NotificationResource notificationRes);


    @GetMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.OK)
    public NotificationResource readOneNotificationEndpoint(
        @Valid @PathVariable(value = "notificationId", required = true) Long notificationId
    ) {
        return readOneNotification(notificationId);
    }

    public abstract NotificationResource readOneNotification(
        Long notificationId
    );

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<NotificationResource> searchNotificationsEndpoint(
        @Valid @RequestParam(required = false, name = "eventType") String eventType,
        @Valid @RequestParam(required = false, name = "notificationStatus") String notificationStatus
        
    ) {
        return searchNotifications(eventType, notificationStatus);
    }

    public abstract List<NotificationResource> searchNotifications(
        String eventType,
        String notificationStatus
        
    );

    @DeleteMapping("/{notificationId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDataProductEndpoint(
        @Valid @PathVariable(value = "notificationId", required = true) Long notificationId
    )  {
        deleteDataProduct(notificationId);
    } 

    public abstract void deleteDataProduct(Long notificationId);
}
