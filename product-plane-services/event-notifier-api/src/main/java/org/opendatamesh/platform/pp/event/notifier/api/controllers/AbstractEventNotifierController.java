package org.opendatamesh.platform.pp.event.notifier.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ListenerResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
        value = "/event-notifier"
)
@Validated
@Tag(
        name = "Event Notifier",
        description = "Endpoints associated to Event Notifier operations"
)
public abstract class AbstractEventNotifierController implements EventNotifierController {


    // ===============================================================================
    // POST /event-notifier/listener
    // ===============================================================================

    @Operation(
            summary = "Add a Listener",
            description = "Add a single listening Notification Adapter"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            value = "/listener",
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
    public ListenerResource addListenerEndpoint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A Listener JSON object"/*,
                    content = @Content(examples = {@ExampleObject(
                            name = "listener-creation-example",
                            description = "Example of a listening Notification Adapter",
                            value = EXAMPLE_LISTENER_CREATE
                    )})*/)
            @RequestBody(required = false) ListenerResource listener
    ) {
        return addListener(listener);
    }

    public abstract ListenerResource addListener(ListenerResource listenerResource);


    // ===============================================================================
    // DELETE /event-notifier/listener/{id}
    // ===============================================================================

    @Operation(
            summary = "Remove a Listener",
            description = "Remove a single listening Notification Adapter given its ID"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @DeleteMapping(value = "/listener/{id}")
    public void removeListenerEndpoint(
            @Parameter(description = "ID of the Listener to delete")
            @PathVariable(value = "id") Long id
    ) {
        removeListener(id);
    }

    public abstract void removeListener(Long id);


    // ===============================================================================
    // DELETE /event-notifier/dispatch
    // ===============================================================================

    @Operation(
            summary = "Add a Listener",
            description = "Add a single listening Notification Adapter"
    )
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(
            value = "/listener",
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
    public void notifyEventEndpoint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A Listener JSON object"/*,
                    content = @Content(examples = {@ExampleObject(
                            name = "event-dispatch-example",
                            description = "Example of a dispatchable Event",
                            value = EXAMPLE_EVENT_DISPATCH
                    )})*/)
            @RequestBody(required = false) EventResource event
    ) {
        notifyEvent(event);
    }

    public abstract void notifyEvent(ListenerResource listenerResource);

}
