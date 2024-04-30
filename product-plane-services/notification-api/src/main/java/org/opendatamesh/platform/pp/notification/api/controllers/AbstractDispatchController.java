package org.opendatamesh.platform.pp.notification.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/dispatch"
)
@Validated
@Tag(
        name = "Dispatch",
        description = "Endpoints associated to dispatching of events to Observers"
)
public abstract class AbstractDispatchController {

    // ===============================================================================
    // Resource examples
    // ===============================================================================

    private static final String EXAMPLE_EVENT_DISPATCH = "{\n" + //
            "   \"type\": \"DATA_PRODUCT_CREATED\",\n" + //
            "   \"entityId\": \"abc123\",\n" + //
            "   \"beforeState\": null,\n" + //
            "   \"beforeState\": \"{\"id\":1, \"name\": \"DP abc 123\"}\",\n" + //
            "   \"time\": \"2024-03-21T12:04:11.000+00:00\"\n" + //
            "}";


    // ===============================================================================
    // POST /dispatch
    // ===============================================================================

    @Operation(
            summary = "Dispatch an Event",
            description = "Dispatch an Event to all registered Observers"
    )
    @ResponseStatus(HttpStatus.OK)
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Event correctly dispatched to all registered Observers"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40002 - Event is empty",
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
            }
    )
    public void notifyEventEndpoint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "An Event JSON object",
                    content = @Content(examples = {@ExampleObject(
                            name = "event-dispatch-example",
                            description = "Example of a dispatchable Event",
                            value = EXAMPLE_EVENT_DISPATCH
                    )}))
            @RequestBody(required = false) EventResource event
    ) {
        notifyEvent(event);
    }

    public abstract void notifyEvent(EventResource eventResource);

}
