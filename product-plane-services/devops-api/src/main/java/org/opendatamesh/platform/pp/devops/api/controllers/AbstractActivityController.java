package org.opendatamesh.platform.pp.devops.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import javax.validation.Valid;

@RestController
@RequestMapping(
        value = "/activities",
        produces = { "application/json" }
)
@Validated
@Tag(
    name = "Activities",
    description = "Endpoints associated to activities collection"
)
public abstract class AbstractActivityController {

    @PostMapping(consumes = { "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", "application/json"})
    @ResponseStatus(HttpStatus.CREATED) 
    @Operation(
            summary = "Create a new activity",
            description = "Create new activity"
    )
     @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Activity createdd", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = ActivityResource.class)
            )
        )})
    public ActivityResource createActivityEndpoint(
        @Parameter( 
            description = "An activity object", 
            required = true)
        @Valid @RequestBody ActivityResource activity, 

        @Parameter(
            description="Pass true to start the activity after creation")
        @RequestParam(required = false, name = "startAfterCreation") boolean startAfterCreation) 
    {
        return createActivity(activity, startAfterCreation);
    }

    public abstract ActivityResource createActivity(
        ActivityResource activity, 
        boolean startAfterCreation
    );


    @PostMapping(
        value = "/{id}/start"
    )
    @Operation(
        summary = "Start the specified activity",
        description = "Start the activity identified by the input `id` if it has not been started yet"
    )
    public ActivityResource startActivityEndpoint(Long id) {
        return startActivity(id);
    }
    public abstract ActivityResource startActivity(Long id);
    
    @GetMapping(
        value = "/{id}/status"
    )
    @ResponseStatus(HttpStatus.OK) 
    @Operation(
        summary = "Get the specified activity's status",
        description = "Get the status of activity identified by the input `id`"
    )
    public String readActivityStatusEndpoint(Long id) {
        return readActivityStatus(id);
    }
    public abstract String readActivityStatus(Long id);


    @GetMapping
    @ResponseStatus(HttpStatus.OK) 
    @Operation(
        summary = "Get all activities",
        description = "Get all ativities"
    )
    public List<ActivityResource> readActivitiesEndpoint() {
        return readActivities();
    }

    public abstract List<ActivityResource>  readActivities();


    @GetMapping(
        value = "/{id}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get the specified activity",
        description = "Get the activity identified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested activity", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = ActivityResource.class)
            )
        )
    })
    public ActivityResource readActivitiyEndpoint(Long id) {
        return readActivitiy(id);
    }

    public abstract ActivityResource readActivitiy(Long id);

}