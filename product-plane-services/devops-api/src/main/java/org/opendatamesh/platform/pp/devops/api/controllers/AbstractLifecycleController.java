package org.opendatamesh.platform.pp.devops.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.devops.api.resources.LifecycleResource;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
        value = "/lifecycles",
        produces = { "application/json" }
)
@Validated
@Tag(
        name = "Lifecycles",
        description = "Endpoints associated to data product's lifecycles collection"
)
public abstract class AbstractLifecycleController {

    // ===============================================================================
    // GET /lifecycles
    // ===============================================================================
    @Operation(
            summary = "Get all lifecycles",
            description = "Get all lifecycles"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All data product lifecycles",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    array = @ArraySchema(schema = @Schema(implementation = LifecycleResource.class))),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    array = @ArraySchema(schema = @Schema(implementation = LifecycleResource.class))),
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LifecycleResource.class)))
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
    public List<LifecycleResource> readLifecyclesEndpoint(
    ) {
        return readLifecycles();
    }

    public abstract List<LifecycleResource> readLifecycles();


    // ===============================================================================
    // GET /lifecycles/{dataProductId}
    // ===============================================================================
    @Operation(
            summary = "Get all lifecycles for the specified data product",
            description = "Get all the lifecycles associated to the input `dataProductId`"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All lifecycles for the specified data product",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    array = @ArraySchema(schema = @Schema(implementation = LifecycleResource.class))),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    array = @ArraySchema(schema = @Schema(implementation = LifecycleResource.class))),
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LifecycleResource.class)))
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
            value = "/{dataProductId}",
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public List<LifecycleResource> readDataProductLifecyclesEndpoint(
            @Parameter(description = "Identifier of the data product")
            @Valid @PathVariable(value = "dataProductId") String dataProductId)
    {
        return readDataProductLifecycles(dataProductId);
    }

    public abstract List<LifecycleResource> readDataProductLifecycles(String dataProductId);


    // ===============================================================================
    // GET /lifecycles/{dataProductId}/{versionNumber}
    // ===============================================================================
    @Operation(
            summary = "Get the lifecycles for the specified tuple <data product - version number>",
            description = "Get the lifecycles identified by the input `dataProductId` and `versionNumber`"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All lifecycles for the specified data product",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    array = @ArraySchema(schema = @Schema(implementation = LifecycleResource.class))),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    array = @ArraySchema(schema = @Schema(implementation = LifecycleResource.class))),
                            @Content(mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = LifecycleResource.class)))
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
            value = "/{dataProductId}/{versionNumber}",
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public List<LifecycleResource> readDataProductVersionLifecyclesEndpoint(
            @Parameter(description = "Identifier of the data product")
            @Valid @PathVariable(value = "dataProductId") String dataProductId,
            @Parameter(description = "Version number of the data product")
            @Valid @PathVariable(value = "versionNumber") String versionNumber)
    {
        return readDataProductVersionLifecycles(dataProductId, versionNumber);
    }

    public abstract List<LifecycleResource> readDataProductVersionLifecycles(String dataProductId, String versionNumber);


    // ===============================================================================
    // GET /lifecycles/{dataProductId}/{versionNumber}/current
    // ===============================================================================
    @Operation(
            summary = "Get the current lifecycle for the specified tuple <data product - version number>",
            description = "Get the current lifecycle identified by the input `dataProductId` and `versionNumber`"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The current lifecycle for the specified data product",
                    content = {
                            @Content(mediaType = "application/vnd.odmp.v1+json",
                                    schema = @Schema(implementation = LifecycleResource.class)),
                            @Content(mediaType = "application/vnd.odmp+json",
                                    schema = @Schema(implementation = LifecycleResource.class)),
                            @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LifecycleResource.class))
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
            value = "/{dataProductId}/{versionNumber}/current",
            produces = {
                    "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json",
                    "application/json"
            }
    )
    public LifecycleResource readDataProductVersionCurrentLifecycleEndpoint(
            @Parameter(description = "Identifier of the data product")
            @Valid @PathVariable(value = "dataProductId") String dataProductId,
            @Parameter(description = "Version number of the data product")
            @Valid @PathVariable(value = "versionNumber") String versionNumber)
    {
        return readDataProductVersionCurrentLifecycle(dataProductId, versionNumber);
    }

    public abstract LifecycleResource readDataProductVersionCurrentLifecycle(String dataProductId, String versionNumber);

}
