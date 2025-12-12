package org.opendatamesh.platform.pp.policy.server.adapter;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.resources.NotificationV2Res;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequestMapping(value = "/api/v2/up/observer", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Data Products", description = "Endpoints for events")
public class PolicyV2AdapterObserver {

    @Autowired
    private PolicyV2AdapterService adapterService;

    @Operation(summary = "Receive a notification event", description = "Receives a notification event from an observer server and dispatches it to the appropriate use case")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Notification event received and dispatched successfully",
                    content = @Content(schema = @Schema(implementation = NotificationV2Res.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request parameters"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/notifications")
    @ResponseStatus(HttpStatus.OK)
    public void processNotification(
            @Parameter(description = "Notification event", required = true)
            @RequestBody NotificationV2Res notification
    ) {
        adapterService.processNotification(notification);
    }
}
