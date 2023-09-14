package org.opendatamesh.platform.pp.registry.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.resources.OwnerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
        value =  "/owners",
        produces = {
                "application/vnd.odmp.v1+json",
                "application/vnd.odmp+json",
                "application/json"
        }
)
@Validated
@Tag(
        name = "Owners",
        description = "Owners")
public abstract class AbstractOwnerController
{

    private static final Logger logger = LoggerFactory.getLogger(AbstractOwnerController.class);

    public AbstractOwnerController() {
        logger.debug("Owner controller successfully started");
    }


    // ======================================================================================
    // OWNERS
    // ======================================================================================

    // ----------------------------------------
    // CREATE Owner
    // ----------------------------------------

    @PostMapping(
            consumes = { "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Register the Owner",
            description = "Register the provided Owner in the Owner Registry"

    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Owner registered",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OwnerResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42215 - Owner is invalid"
                            + "\r\n - Error Code 42216 - Owner already exists",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50001 - Error in the backend database"
                            + "\r\n - Error Code 50002 - Error in in the backend service",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    public OwnerResource createOwnerEndpoint(
            @Parameter(
                    description = "A Owner object",
                    required = true)
            @Valid @RequestBody(required=false) OwnerResource ownerResource
    ) throws Exception {
        return createOwner(ownerResource);
    }

    public abstract OwnerResource createOwner(OwnerResource ownerResource) throws Exception;


    // ----------------------------------------
    // READ All Owner
    // ----------------------------------------


    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get all registered owners",
            description = "Get all owners registered in the Owner Registry."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All registered owners",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OwnerResource.class)
                    )
            )
    })
    public List<OwnerResource> getAllOwnersEndpoint()
    {
        return getAllOwners();
    }
    public abstract List<OwnerResource> getAllOwners();

    // ----------------------------------------
    // READ Owner
    // ----------------------------------------

    // TODO add all error responses

    @GetMapping(
            value = "/{id}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get the specified owner",
            description = "Get the owner identified by the input `id`"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested owner",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OwnerResource.class)
                    )
            )
    })
    public OwnerResource getOwnerEndpoint(
            @Parameter(description = "Identifier of the owner")
            @Valid @PathVariable(value = "id") String id)
    {
        return getOwner(id);
    }

    public abstract OwnerResource getOwner(String id);

    // ----------------------------------------
    // DELETE Owner
    // ----------------------------------------


    @RequestMapping(
            value = "/{id}",
            method = RequestMethod.DELETE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Delete the specified owner",
            description = "Delete the owner identified by the input `id`"

    )
    public void deleteOwnerEndpoint(
            @Parameter(description = "Identifier of the owner")
            @PathVariable String id
    ) {
        deleteOwner(id);
    }

    public abstract void deleteOwner(String id) ;

    // ----------------------------------------
    // UPDATE Owner
    // ----------------------------------------

    @PutMapping(
            consumes = { "application/vnd.odmp.v1+json", "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update an owner",
            description = "Update the provided owner"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Owner updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = OwnerResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40406 - Owner not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42215 - Owner is invalid",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50001 - Error in the backend database"
                            + "\r\n - Error Code 50002 - Error in in the backend service",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    public OwnerResource updateOwnerEndpoint(
            @Parameter(
                    description = "A Owner object",
                    required = true)
            @Valid @RequestBody(required=false)  OwnerResource ownerResource
    ) throws Exception {
        return updateOwner(ownerResource);
    }

    public abstract OwnerResource updateOwner(OwnerResource ownerResource) throws Exception;
}
