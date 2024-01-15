package org.opendatamesh.platform.up.executor.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(
    value = "/tasks"
)
@Validated
@Tag(
    name = "Tasks",
    description = "Endpoints associated to `tasks` collection"
)
public abstract class AbstractExecutorController {

    private static final String EXAMPLE_ONE = "{\n" + //
            "    \"callbackRef\": \"my/callback/url\",\n" + //
            "    \"template\": \"{\\\"organization\\\":\\\"andreagioia\\\",\\\"project\\\":\\\"opendatamesh\\\",\\\"pipelineId\\\":\\\"3\\\",\\\"branch\\\":\\\"main\\\"}\",\n" + //
            "    \"configurations\": \"{\\\"stagesToSkip\\\":[]}\"\n" + //
            "}";

    private static final TaskResource EXAMPLE_TWO = new TaskResource();
    static {
        EXAMPLE_TWO.setCallbackRef("my/callback/url");
        EXAMPLE_TWO.setTemplate("\"{\\\"organization\\\":\\\"andreagioia\\\",\\\"project\\\":\\\"opendatamesh\\\",\\\"pipelineId\\\":\\\"3\\\",\\\"branch\\\":\\\"main\\\"}\"");
        EXAMPLE_TWO.setConfigurations("\"{\\\"stagesToSkip\\\":[]}\"");
    }

    // @see https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations#arrayschema

    // ===============================================================================
    // POST /tasks  
    // ===============================================================================
    @Operation(
        summary = "Execute task",
        description = "Execute the provided task"
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Task created and started", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = TaskResource.class),
                examples = {
                    @ExampleObject(name = "one", value = EXAMPLE_ONE)}
            )
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "[Conflict](https://www.rfc-editor.org/rfc/rfc9110.html#name-409-conflict)"
            + "\r\n - Error Code 40901 - Task is already started",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
            + "\r\n - Error Code 42201 - Task is invalid",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
            + "\r\n - Error Code 50001 - Error in in the backend service" 
            + "\r\n - Error Code 50050 - Azure Devops API or not reachable",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ), 
        @ApiResponse(
            responseCode = "501", 
            description = "[Bad Gateway](https://www.rfc-editor.org/rfc/rfc9110.html#name-502-bad-gateway)"
            + "\r\n - Error Code 50250 - Azure Devops API returns an error",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    @PostMapping(
        consumes = { 
            "application/vnd.odmp.v1+json", 
            "application/vnd.odmp+json", 
            "application/json"},
        produces = { 
            "application/vnd.odmp.v1+json", 
            "application/vnd.odmp+json", 
            "application/json"
        }
    )
    public TaskResource createTaskEndpoint(@RequestBody TaskResource task) {
        return createTask(task);
    }  
    
    public abstract TaskResource createTask(TaskResource task);

    // ===============================================================================
    // GET /tasks
    // ===============================================================================
    @Operation(
        summary = "Get the task updated version",
        description = "Get the an updated version of the given task"
    )
    @PostMapping(
        value = "/update"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested task updated version", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = TaskResource.class)
            )
        )
    })
    public TaskResource readTaskEndpoint(@RequestBody TaskResource task) {
        return readTask(task);
    }  
    
    public abstract TaskResource readTask(TaskResource task);

    // ===============================================================================
    // GET /tasks/{taskId}/status
    // ===============================================================================

    @Operation(
            summary = "Get the task updated version",
            description = "Get the an updated version of the given task"
    )
    @GetMapping(
            value = "/{taskId}/status"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested task status with updated state from Azure",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TaskStatus.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Pipeline run for Task not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorRes.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50050 - Error in in the backend service",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorRes.class)
                            )}
            )
    })
    public TaskStatus readTaskStatusEndpoint(@PathVariable(value = "taskId") Long taskId) {
        return readTaskStatus(taskId);
    }

    public abstract TaskStatus readTaskStatus(Long taskId);

}
