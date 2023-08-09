package org.opendatamesh.platform.pp.devops.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping(
    value = "/tasks",
    produces = { "application/json" }
)
@Validated
@Tag(
    name = "Task",
    description = "Endpoints associated to tasks collection"
)
public abstract class AbstractTaskController {

   
    @PostMapping(
        value = "/{id}/stop"
    )
    @Operation(
        summary = "Stop the specified task",
        description = "Stop the task identified by the input `id` if it has not been stoped yet"
    )
    public ActivityTaskResource stopTaskEndpoint(Long id) {
        return stopTask(id);
    }
    public abstract ActivityTaskResource stopTask(Long id);
    
    @GetMapping(
        value = "/{id}/status"
    )
    @ResponseStatus(HttpStatus.OK) 
    @Operation(
        summary = "Get the specified task's status",
        description = "Get the status of task identified by the input `id`"
    )
    public String readTaskStatusEndpoint(Long id) {
        return readTaskStatus(id);
    }
    public abstract String readTaskStatus(Long id);


    @GetMapping
    @ResponseStatus(HttpStatus.OK) 
    @Operation(
        summary = "Get all tasks",
        description = "Get all tasks"
    )
    public List<ActivityTaskResource> readTasksEndpoint() { // TODO add activity id param
        return readTasks();
    }

    public abstract List<ActivityTaskResource>  readTasks();


    @GetMapping(
        value = "/{id}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get the specified task",
        description = "Get the task identified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested task", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = ActivityTaskResource.class)
            )
        )
    })
    public ActivityTaskResource readActivitiyEndpoint(Long id) {
        return readTask(id);
    }

    public abstract ActivityTaskResource readTask(Long id);
}
