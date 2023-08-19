package org.opendatamesh.platform.pp.devops.api.controllers;

import java.util.List;

import javax.validation.Valid;

import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ErrorRes;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
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

   
    // ===============================================================================
    // POST /tasks  
    // ===============================================================================

    // NOTE Task cannot be created directly

    // ===============================================================================
    // PATCH /tasks/{id}/start  
    // ===============================================================================

     // NOTE Task cannot be started directly

    // ===============================================================================
    // PATCH /tasks/{id}/stop  
    // ===============================================================================
   
    @Operation(
        summary = "Stop the specified task",
        description = "Stop the task identified by the input `id` if it has not been stoped yet"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The task to stop", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = ActivityTaskResource.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n - Error Code 40411 - Task not found",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
            + "\r\n - Error Code 50000 - Error in the backend database"
            + "\r\n - Error Code 50001 - Error in in the backend service" 
            + "\r\n - Error Code 50002 - Error in the backend descriptor processor"
            + "\r\n - Error Code 50050 - Registry service is disabled or not reachable"
            + "\r\n - Error Code 50070 - Notification service is disabled or not reachable" 
            + "\r\n - Error Code 50071 - Policy service is disabled or not reachable"
            + "\r\n - Error Code 50072 - Executor service is disabled or not reachable", 
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ), 
        @ApiResponse(
            responseCode = "501", 
            description = "[Bad Gateway](https://www.rfc-editor.org/rfc/rfc9110.html#name-502-bad-gateway)"
            + "\r\n - Error Code 50250 - Registry service returns an error" 
            + "\r\n - Error Code 50270 - Notification service returns an error"
            + "\r\n - Error Code 50271 - Policy service returns an error"
            + "\r\n - Error Code 50272 - Executor service returns an error",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    @PatchMapping(
        value = "/{id}/stop",
        produces = { 
            "application/vnd.odmp.v1+json", 
            "application/vnd.odmp+json", 
            "application/json"
        }
    )
    public ActivityTaskResource stopTaskEndpoint( 
        @Parameter(description = "Idenntifier of the task")
        @Valid @PathVariable(value = "id") Long id) 
    {
        return stopTask(id);
    }
    public abstract ActivityTaskResource stopTask(Long id);
    
    // ===============================================================================
    // GET /tasks/{id}/status  
    // ===============================================================================
    @Operation(
        summary = "Get the specified task's status",
        description = "Get the status of task identified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The status of the requested task", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = String.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n - Error Code 40411 - Task not found",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
            + "\r\n - Error Code 50000 - Error in the backend database"
            + "\r\n - Error Code 50001 - Error in in the backend service" 
            + "\r\n - Error Code 50002 - Error in the backend descriptor processor",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    @GetMapping(
        value = "/{id}/status",
        produces = { 
            "text/plain"
        }
    )
    @ResponseStatus(HttpStatus.OK) 
    
    public String readTaskStatusEndpoint( 
        @Parameter(description = "Idenntifier of the task")
        @Valid @PathVariable(value = "id") Long id) 
    {
        return readTaskStatus(id);
    }
    public abstract String readTaskStatus(Long id);


    // ===============================================================================
    // GET /tasks
    // ===============================================================================
    @Operation(
        summary = "Get all tasks",
        description = "Get all tasks"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "All created tasks", 
            content = {
                @Content(mediaType = "application/vnd.odmp.v1+json", 
                    array = @ArraySchema(schema = @Schema(implementation = ActivityTaskResource.class))),
                @Content(mediaType = "application/vnd.odmp+json", 
                    array = @ArraySchema(schema = @Schema(implementation = ActivityTaskResource.class))),
                @Content(mediaType = "application/json", 
                    array = @ArraySchema(schema = @Schema(implementation = ActivityTaskResource.class)))
            }
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
            + "\r\n - Error Code 50000 - Error in the backend database"
            + "\r\n - Error Code 50001 - Error in in the backend service" 
            + "\r\n - Error Code 50002 - Error in the backend descriptor processor",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    @GetMapping(
        produces = { 
            "application/vnd.odmp.v1+json", 
            "application/vnd.odmp+json", 
            "application/json"
        }
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


    // ===============================================================================
    // GET /tasks/{id}
    // ===============================================================================
    @Operation(
        summary = "Get the specified task",
        description = "Get the task identified by the input `id`"
    )    
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested task", 
            content = {
                @Content(mediaType = "application/vnd.odmp.v1+json", 
                    schema = @Schema(implementation = ActivityTaskResource.class)),
                @Content(mediaType = "application/vnd.odmp+json", 
                    schema = @Schema(implementation = ActivityTaskResource.class)),
                @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ActivityTaskResource.class))
            }
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n - Error Code 40411 - Task not found",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
            + "\r\n - Error Code 50000 - Error in the backend database"
            + "\r\n - Error Code 50001 - Error in in the backend service" 
            + "\r\n - Error Code 50002 - Error in the backend descriptor processor",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    @GetMapping(
        value = "/{id}",
        produces = { 
            "application/vnd.odmp.v1+json", 
            "application/vnd.odmp+json", 
            "application/json"
        }
    )
    public ActivityTaskResource readActivitiyEndpoint( 
        @Parameter(description = "Idenntifier of the task")
        @Valid @PathVariable(value = "id") Long id) 
    {
        return readTask(id);
    }

    public abstract ActivityTaskResource readTask(Long id);
}
