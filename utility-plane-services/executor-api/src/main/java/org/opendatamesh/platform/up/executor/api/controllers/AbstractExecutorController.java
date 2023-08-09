package org.opendatamesh.platform.up.executor.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
@Validated
public abstract class AbstractExecutorController {

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
            summary = "Create a new activity",
            description = "Create new activity"
    )
    public TaskResource createTaskEndpoint(@RequestBody TaskResource task) {
        return createTask(task);
    }  
    
    public abstract TaskResource createTask(TaskResource task);

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
                schema = @Schema(implementation = TaskResource.class)
            )
        )
    })
    public TaskResource readTaskEndpoint(TaskResource task) {
        return task;
    }  
    
    public abstract TaskResource readTask(Long id);
}
