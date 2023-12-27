package org.opendatamesh.platform.pp.registry.api.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.model.interfaces.InterfaceComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.ApplicationComponentDPDS;
import org.opendatamesh.platform.pp.registry.api.resources.VariableResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(
    value =  "/products", 
    produces = { "application/vnd.odmp.v1+json", "application/vnd.odmp+json", "application/json"}
)
@Validated
@Tag(
    name = "Data Product Components", 
    description = "Data Product Components")
public abstract class AbstractDataProductVersionComponentsController 
{

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataProductVersionComponentsController.class);

    public AbstractDataProductVersionComponentsController() { 
        logger.debug("Data product version components controller successfully started");
    }

  
    // ======================================================================================
    // GET /{id}/versions/{version}/ports
    // ======================================================================================
    
    @RequestMapping(
        value = "/{id}/versions/{version}/ports", 
        method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get all the ports of the specified data product version",
        description = "Get all the ports of the specified data product version grouped by port type."
        //, tags = { "Data Product Components" }
    ) 
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "TODO: the api definition", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = InterfaceComponentsDPDS.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "[Bad request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
            + "\r\n - Invalid port type" 
            + "\r\n - Invalid format", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n -  Data product does not exists"
            + "\r\n -  Data product's version does not exists", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
        )
    })
    public String getPortsEndpoint(
        @Parameter(description = "The identifier of the data product")
        @PathVariable(value = "id") String id,
        
        @Parameter(description = "The version number")
        @PathVariable(value = "version") String version,

        @RequestParam(name = "portType", required = false) String portType,

        @RequestParam(name = "format", required = false) String format
       
    )   {
        return getPorts(id, version, portType, format);
    }


    public abstract String getPorts(String id, String version, String portType, String format);

    // ======================================================================================
    // GET /{id}/versions/{version}/applications
    // ======================================================================================

    @GetMapping(
            value = "/{id}/versions/{version}/applications"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get the application components in the internal components for the specified data product version",
            description = "Get the application components in the internal components for the data product version identified by `id` and 'version'."
                    + "The returned value is a descriptor component document compliant with [DPDS version 1.0.0-DRAFT](https://dpds.opendatamesh.org/resources/specifications/1.0.0-DRAFT/)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The application components inside the internal components of the specified data product version",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationComponentDPDS.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40004 - Invalid format"
                            + "\r\n - Error Code 40007 - Product id is empty"
                            + "\r\n - Error Code 40011 - Data product version number is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Data product not found"
                            + "\r\n - Error Code 40402 - Data product version not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in in the backend service"
                            + "\r\n - Error Code 50001 - Error in the backend database"
                            + "\r\n - Error Code 50002 - Error in in the backend descriptor processor",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    public String getDataProductVersionApplicationsEndpoint(
            @Parameter(description = "The identifier of the data product")
            @PathVariable(value = "id") String id,

            @Parameter(description = "The data product version number")
            @PathVariable(value = "version") String version,

            @Parameter(
                    description = "Format used to serialize the descriptor component document. Available formats are:"
                            + "\r\n - normalized: TODO"
                            + "\r\n - canonical (default): TODO"
            )
            @RequestParam(name = "format", required = false) String format
    ) {
        return getDataProductVersionApplications(id, version, format);
    }

    public abstract String getDataProductVersionApplications(String id, String version, String format);

    // ======================================================================================
    // GET /{id}/versions/{version}/infrastructures
    // ======================================================================================

    @GetMapping(
            value = "/{id}/versions/{version}/infrastructures"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get the infrastructure components in the internal components for the specified data product version",
            description = "Get the infrastructure components in the internal components for the data product version identified by `id` and 'version'."
                    + "The returned value is a descriptor component document compliant with [DPDS version 1.0.0-DRAFT](https://dpds.opendatamesh.org/resources/specifications/1.0.0-DRAFT/)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The infrastructure components inside the internal components of the specified data product version",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ApplicationComponentDPDS.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40004 - Invalid format"
                            + "\r\n - Error Code 40007 - Product id is empty"
                            + "\r\n - Error Code 40011 - Data product version number is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Data product not found"
                            + "\r\n - Error Code 40402 - Data product version not found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50000 - Error in in the backend service"
                            + "\r\n - Error Code 50001 - Error in the backend database"
                            + "\r\n - Error Code 50002 - Error in in the backend descriptor processor",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    public String getDataProductVersionInfrastructuresEndpoint(
            @Parameter(description = "The identifier of the data product")
            @PathVariable(value = "id") String id,

            @Parameter(description = "The data product version number")
            @PathVariable(value = "version") String version,

            @Parameter(
                    description = "Format used to serialize the descriptor component document. Available formats are:"
                            + "\r\n - normalized : TODO"
                            + "\r\n - canonical (default): TODO"
            )
            @RequestParam(name = "format", required = false) String format
    ) {
        return getDataProductVersionInfrastructures(id, version, format);
    }

    public abstract String getDataProductVersionInfrastructures(String id, String version, String format);


    // ======================================================================================
    // GET /{id}/versions/{version}/variables
    // ======================================================================================

    @RequestMapping(
            value = "/{id}/versions/{version}/variables",
            method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get all the variables of the specified data product version",
            description = "Get all the existing variables of the specified data product version."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "All existing variables of the data product version",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = InterfaceComponentsDPDS.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40402 - Data product version does not exists",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ErrorRes.class)
                    )
            )
    })
    public List<VariableResource> getVariablesEndpoint(
            @Parameter(description = "The identifier of the data product")
            @PathVariable(value = "id") String id,

            @Parameter(description = "The version number")
            @PathVariable(value = "version") String version
    )   {
        return getVariables(id, version);
    }

    public abstract List<VariableResource> getVariables(String id, String version);


    // ======================================================================================
    // PUT /{id}/versions/{version}/variables/{varId}
    // ======================================================================================

    @PutMapping(
            consumes = { "application/vnd.odmp.v1+json", "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Update a variable of a data product version",
            description = "Update the value of a specific variable of the provided data product"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Variable updated",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = InterfaceComponentsDPDS.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 4040x - Data Product Version not found"
                            + "\r\n - Error Code 4040x - Variable of Data Product Version not found",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorRes.class)
                            )}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50001 - Error in the backend database"
                            + "\r\n - Error Code 50002 - Error in in the backend service",
                    content = {
                            @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ErrorRes.class)
                            )}
            )
    })
    public VariableResource updateProductEndpoint(
            @Parameter(description = "The identifier of the data product")
            @PathVariable(value = "id") String id,

            @Parameter(description = "The version number")
            @PathVariable(value = "version") String version,

            @Parameter(description = "The identifier of the variable")
            @PathVariable(value = "variableId") Long variableId,

            @Parameter(description="The new value of the variable")
            @RequestParam(name = "value") String variableValue
    ) {
        return updateVariable(id, version, variableId, variableValue);
    }

    public abstract VariableResource updateVariable(String id, String version, Long variableId, String variableValue);

}
