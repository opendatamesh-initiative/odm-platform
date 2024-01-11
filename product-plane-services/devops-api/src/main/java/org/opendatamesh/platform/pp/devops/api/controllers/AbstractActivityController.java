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
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatusResource;
import org.opendatamesh.platform.pp.devops.api.resources.DevOpsApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
    value = "/activities"
)
@Validated
@Tag(
    name = "Activities",
    description = "Endpoints associated to activities collection"
)
public abstract class AbstractActivityController {

    private static final String EXAMPLE_ONE = "{\n" + //
            "    \"dataProductId\": \"c18b07ba-bb01-3d55-a5bf-feb517a8d901\",\n" + //
            "    \"dataProductVersion\": \"1.0.0\",\n" + //
            "    \"stage\": \"prod\"\n" + //
            "}";

    // @see https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations#arrayschema

    // ===============================================================================
    // POST /activities  
    // ===============================================================================
    @Operation(
        summary = "Create a new activity",
        description = "Create new activity of the given type and all its associated tasks on the specified data product. "
        + "By default the activity and all it's tasks after creation are in `PLANNED` state. To start the activity after "
        + "creation pass also the parameter `startAfterCreation` set to `true` or call the proper endpoint (see below)"
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201",
            description = "Activity created", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = ActivityResource.class),
                examples = {
                    @ExampleObject(name = "one", value = EXAMPLE_ONE)}
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
            + "\r\n - Error Code 42201 - Activity is invalid"
            + "\r\n - Error Code 42202 - Activity already exists",  
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
    public ActivityResource createActivityEndpoint(
        @io.swagger.v3.oas.annotations.parameters.RequestBody( 
            description = "An activity object", 
            required = true,
            content = @Content(
                examples = {
                    @ExampleObject(name = "one", description = "description of example one", value = EXAMPLE_ONE)}
            )
        )
        @RequestBody ActivityResource activity,
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

    // ===============================================================================
    // PATCH /activities/{id}/status?action=start  
    // ===============================================================================
    @Operation(
        summary = "Start the specified activity",
        description = "Start the activity identified by the input `id` if it has not been started yet"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The status of the activity after the action execution", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = ActivityStatusResource.class)
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
            + "\r\n - Error Code 40055 - Activity status action is invalid",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n - Error Code 40401 - Activity not found",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "409", 
            description = "[Conflict](https://www.rfc-editor.org/rfc/rfc9110.html#name-409-conflict)"
            + "\r\n - Error Code 40901 - There is already a running activity on given product version",
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
        produces = { 
            "application/vnd.odmp.v1+json", 
            "application/vnd.odmp+json", 
            "application/json"
        }
    )
    public ActivityStatusResource startActivityEndpoint( 
        @Parameter(description = "Identifier of the activity")
        @Valid @PathVariable(value = "id") Long id,

        @Parameter(description="Add `action` parameter to the request to specify which action to perform to change the activity's status. `START` is the only possible action executable on activities")
        @RequestParam(required = true, name = "action") String action)
    {
        if("START".equalsIgnoreCase(action) == false) {
            throw new BadRequestException(
                DevOpsApiStandardErrors.SC400_55_ACTIVITY_STATUS_ACTION_IS_INVALID, 
                "Action [" + action + "] cannot be performend on activity to change its status");
        }
        return startActivity(id);
    }
    public abstract ActivityStatusResource startActivity(Long id);
    

    // ===============================================================================
    // GET /activities/{id}/status  
    // ===============================================================================
    @Operation(
        summary = "Get the specified activity's status",
        description = "Get the status of activity identified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The status of the requested activity", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = ActivityStatusResource.class)
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n - Error Code 40401 - Activity not found",
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
    public ActivityStatusResource readActivityStatusEndpoint( 
        @Parameter(description = "Identifier of the activity")
        @Valid @PathVariable(value = "id") Long id) 
    {
        return readActivityStatus(id);
    }
    public abstract ActivityStatusResource readActivityStatus(Long id);

    // ===============================================================================
    // GET /activities
    // ===============================================================================
    @Operation(
        summary = "Get all activities",
        description = "Get all the created activities"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "All created activities", 
            content = {
                @Content(mediaType = "application/vnd.odmp.v1+json", 
                    array = @ArraySchema(schema = @Schema(implementation = ActivityResource.class))),
                @Content(mediaType = "application/vnd.odmp+json", 
                    array = @ArraySchema(schema = @Schema(implementation = ActivityResource.class))),
                @Content(mediaType = "application/json", 
                    array = @ArraySchema(schema = @Schema(implementation = ActivityResource.class)))
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
    public List<ActivityResource> readActivitiesEndpoint(
        @Parameter(description="Add `dataProductId` parameter to the request to get only activities associated to a specific data product")
        @RequestParam(required = false, name = "dataProductId") String dataProductId,

        @Parameter(description="Add `dataProductVersion` parameter to the request to get only activities associated to a specific data product version")
        @RequestParam(required = false, name = "dataProductVersion") String dataProductVersion,

        @Parameter(description="Add `stage` parameter to the request to get only activities of the specific stage")
        @RequestParam(required = false, name = "stage") String stage,
       
        @Parameter(description="Add `status` parameter to the request to get only activities in the specific state")
        @RequestParam(required = false, name = "status") ActivityStatus status
    ) {
        return readActivities(dataProductId, dataProductVersion, stage, status);
    }

    public abstract List<ActivityResource> readActivities(String dataProductId, String dataProductVersion, String stage, ActivityStatus status);


    // ===============================================================================
    // GET /activities/{id}
    // ===============================================================================
    @Operation(
        summary = "Get the specified activity",
        description = "Get the activity identified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested activity", 
            content = {
                @Content(mediaType = "application/vnd.odmp.v1+json", 
                    schema = @Schema(implementation = ActivityResource.class)),
                @Content(mediaType = "application/vnd.odmp+json", 
                    schema = @Schema(implementation = ActivityResource.class)),
                @Content(mediaType = "application/json", 
                    schema = @Schema(implementation = ActivityResource.class))
            }
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n - Error Code 40401 - Activity not found",
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
    public ActivityResource readActivitiyEndpoint( 
        @Parameter(description = "Identifier of the activity")
        @Valid @PathVariable(value = "id") Long id) 
    {
        return readActivitiy(id);
    }

    public abstract ActivityResource readActivitiy(Long id);

}
