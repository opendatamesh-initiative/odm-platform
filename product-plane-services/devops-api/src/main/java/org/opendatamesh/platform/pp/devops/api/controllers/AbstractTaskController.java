package org.opendatamesh.platform.pp.devops.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.devops.api.resources.*;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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

    private static final String EXAMPLE_PROCESSED = "{\n" + //
            "    \"status\": \"PROCESSED\",\n" + //
            "    \"results\": {\n" +
            "        \"message\": \"Task executed successfully\"\n" + //
            "        \"param\": \"param_value\"\n" + //
            "    }\n" + //
            "}";

    private static final String EXAMPLE_FAILED = "{\n" + //
            "    \"status\": \"FAILED\",\n" + //
            "    \"errors\": \"Error messages from the executor\"\n" + //
            "}";


    // ===============================================================================
    // POST /tasks  
    // ===============================================================================

    // NOTE Task cannot be created directly

    // ===============================================================================
    // PATCH /tasks/{id}/start  
    // ===============================================================================

     // NOTE Task cannot be started directly

    // ===============================================================================
    // PATCH /tasks/{id}/status?action=stop 
    // ===============================================================================

    @Operation(
            summary = "Change the status of a task",
            description = "Use this endpoint to change the status of a task. Supported actions via the `action` request parameter are:\n" +
                    "- `START`: starts the task (no request body required).\n" +
                    "- `STOP`: stops the task and optionally provides a TaskResult body.\n" +
                    "- `ABORT`: aborts the task (no request body required).\n\n" +
                    "Use `updateVariables=false` to skip result-based variable extraction (only applies to STOP)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The status of the task after the action execution",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TaskStatusResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40065 - Task status action is invalid",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
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
            value = "/{id}/status",
            consumes = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json",
                    "application/json;charset=UTF-8"
            },
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public TaskStatusResource stopTaskEndpoint(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "A TaskResult object (only required for STOP action)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "successful-example",
                                            description = "Example for a succeeded task",
                                            value = EXAMPLE_PROCESSED
                                    ),
                                    @ExampleObject(
                                            name = "error-example",
                                            description = "Example for a failed Task",
                                            value = EXAMPLE_FAILED
                                    )
                            }
                    )
            )
            @RequestBody(required = false) TaskResultResource taskResult,

            @Parameter(description = "Identifier of the task")
            @Valid @PathVariable(value = "id") Long id,

            @Parameter(description = "Action to perform on the task. Supported values: START, STOP, ABORT")
            @RequestParam(required = true, name = "action") String action,

            @Parameter(description = "Set `updateVariables=false` to prevent variable extraction from the result. Applies only to STOP. Default is `true`.")
            @RequestParam(required = false, name = "updateVariables") Boolean updateVariables
    ) {
        if (updateVariables == null)
            updateVariables = true;
        if ("START".equalsIgnoreCase(action)) {
            return startTask(id);
        } else if ("ABORT".equalsIgnoreCase(action)) {
            return abortTask(id);
        } else if ("STOP".equalsIgnoreCase(action)) {
            return stopTask(id, taskResult, updateVariables);
        } else {
            throw new BadRequestException(
                    DevOpsApiStandardErrors.SC400_65_TASK_STATUS_ACTION_IS_INVALID,
                    "Action [" + action + "] cannot be performend on task to change its status");
        }
    }
    public abstract TaskStatusResource stopTask(Long id, TaskResultResource taskResultResource, Boolean updateVariables);

    public abstract TaskStatusResource startTask( Long id);

    public abstract TaskStatusResource abortTask( Long id);
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
                schema = @Schema(implementation = TaskStatusResource.class)
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
            "application/vnd.odmp.v1+json",
            "application/vnd.odmp+json",
            "application/json"
        }
    )
    @ResponseStatus(HttpStatus.OK)

    public TaskStatusResource readTaskStatusEndpoint(
            @Parameter(description = "Identifier of the task")
            @Valid @PathVariable(value = "id") Long id)
    {
        return readTaskStatus(id);
    }
    public abstract TaskStatusResource readTaskStatus(Long id);


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
        @Parameter(description = "Identifier of the task")
        @Valid @PathVariable(value = "id") Long id)
    {
        return readTask(id);
    }

    public abstract ActivityTaskResource readTask(Long id);
}
