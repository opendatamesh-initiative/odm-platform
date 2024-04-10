package org.opendatamesh.platform.pp.event.notifier.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
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
    // POST /dispatch
    // ===============================================================================

    @Operation(
            summary = "Dispatch an Event",
            description = "Dispatch an Event to all registered Observers"
    )
    @ResponseStatus(HttpStatus.OK)
    @PostMapping(
            consumes = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public void notifyEventEndpoint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "An Event JSON object"/*,
                    content = @Content(examples = {@ExampleObject(
                            name = "event-dispatch-example",
                            description = "Example of a dispatchable Event",
                            value = EXAMPLE_EVENT_DISPATCH
                    )})*/)
            @RequestBody(required = false) EventResource event
    ) {
        notifyEvent(event);
    }

    public abstract void notifyEvent(EventResource eventResource);

}
