package org.opendatamesh.platform.pp.event.notifier.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
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
    // POST /observers
    // ===============================================================================

    @Operation(
            summary = "Add an Observer",
            description = "Add a single listening Notification Adapter (i.e., an Observer)"
    )
    @ResponseStatus(HttpStatus.CREATED)
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
                    description = "An Observer JSON object"/*,
                    content = @Content(examples = {@ExampleObject(
                            name = "observer-creation-example",
                            description = "Example of a listening Notification Adapter",
                            value = EXAMPLE_OBSERVER_CREATE
                    )})*/)
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
    @PutMapping(
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
            @RequestParam Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "An Observer JSON object"/*,
                    content = @Content(examples = {@ExampleObject(
                            name = "observer-creation-example",
                            description = "Example of a listening Notification Adapter",
                            value = EXAMPLE_OBSERVER_UPDATE
                    )})*/)
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
            summary = "Get a PolicyEngine",
            description = "Get the PolicyEngine identified by the given ID"
    )
    @ResponseStatus(HttpStatus.OK)
    @GetMapping(
            value = "/{id}",
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public ObserverResource getObserverEndpoint(
            @Parameter(description = "", required = true)
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
    @ResponseStatus(HttpStatus.CREATED)
    @DeleteMapping(value = "/{id}")
    public void removeObserverEndpoint(
            @Parameter(description = "ID of the Observer to delete")
            @PathVariable(value = "id") Long id
    ) {
        removeObserver(id);
    }

    public abstract void removeObserver(Long id);

}
