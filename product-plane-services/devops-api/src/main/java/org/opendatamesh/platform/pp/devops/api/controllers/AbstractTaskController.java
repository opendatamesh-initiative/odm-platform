package org.opendatamesh.platform.pp.devops.api.controllers;

import java.util.List;

import javax.validation.Valid;

import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;


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
    public ActivityTaskResource stopTaskEndpoint( 
        @Parameter(description = "Idenntifier of the task")
        @Valid @PathVariable(value = "id") Long id) 
    {
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
    public String readTaskStatusEndpoint( 
        @Parameter(description = "Idenntifier of the task")
        @Valid @PathVariable(value = "id") Long id) 
    {
        return readTaskStatus(id);
    }
    public abstract String readTaskStatus(Long id);


    @GetMapping
    @ResponseStatus(HttpStatus.OK) 
    @Operation(
        summary = "Get all tasks",
        description = "Get all tasks"
    )
    public List<ActivityTaskResource> readTasksEndpoint(
        @Parameter(description="Add `activityId` parameter to the request to get only tasks associated to a specific activity")
        @RequestParam(required = false, name = "activityId") Long activityId,
        
        @Parameter(description="Add `executor` parameter to the request to get only tasks associated to a specific executor service")
        @RequestParam(required = false, name = "executor") String executor,
        
        @Parameter(description="Add `status` parameter to the request to get only tasks in a specific state")
        @RequestParam(required = false, name = "status") ActivityTaskStatus status
    ) { 
        return readTasks(activityId, executor, status);
    }

    public abstract List<ActivityTaskResource>  readTasks(Long activityId,String executorRef, ActivityTaskStatus status);


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
    public ActivityTaskResource readActivitiyEndpoint( 
        @Parameter(description = "Idenntifier of the task")
        @Valid @PathVariable(value = "id") Long id) 
    {
        return readTask(id);
    }

    public abstract ActivityTaskResource readTask(Long id);
}
