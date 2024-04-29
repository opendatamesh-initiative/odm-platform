package org.opendatamesh.platform.up.observer.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/notifications")
@Validated
public abstract class AbstractConsumeController implements ConsumeController {

    @PostMapping(
        consumes = { "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Consume the notification",
        description = "Send the notification to the Observer and start the handling process"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "Notification received and handled",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = EventNotificationResource.class)
            )
        )
    })
    public EventNotificationResource consumeEventNotificationEndpoint(
        @Parameter( 
            description = "A EventNotification object",
            required = true)
        @Valid @RequestBody EventNotificationResource notificationRes
    ) {
        return consumeEventNotification(notificationRes);
    }

    public abstract EventNotificationResource consumeEventNotification(EventNotificationResource notificationRes);

}
