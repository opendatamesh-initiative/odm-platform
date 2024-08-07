package org.opendatamesh.platform.pp.registry.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
    value =  "/products", 
    produces = { "application/vnd.odmp.v1+json", 
                 "application/vnd.odmp+json", 
                 "application/json"}
)
@Validated
@Tag(
    name = "Data Product Versions", 
    description = "Data Product Versions")
public abstract class AbstractDataProductVersionController
{

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataProductVersionController.class);

    public AbstractDataProductVersionController() { 
        logger.debug("Data product version controller successfully started");
    }


    // ======================================================================================
    // POST /products/{id}/versions
    // ======================================================================================

    @PostMapping(
        value = "/{id}/versions", 
        consumes = {"application/vnd.odmp.v1+json", 
                    "application/vnd.odmp+json", 
                    "application/json"
        }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new data product version",
        description = "Create a new data product version and associate it to the specified data product"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "[Created](https://www.rfc-editor.org/rfc/rfc9110.html#name-201-created)"
            + "\r\n - Data product version created", 
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DataProductVersionDPDS.class))
        }),
        @ApiResponse(
            responseCode = "400", 
            description = "[Bad request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
            + "\r\n - Error Code 40001 - Descriptor is empty"
            + "\r\n - Error Code 40007 - Product id is empty",  
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n - Error Code 40401 - Data product not found", 
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
            + "\r\n - Error Code 42202 - Descriptor document syntax is invalid"
            + "\r\n - Error Code 42203 - Descriptor document semantyc is invalid" 
            + "\r\n - Error Code 42205 - Version already exists",  
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),

        @ApiResponse(
            responseCode = "500", 
            description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
            + "\r\n - Error Code 50000 - Error in in the backend service"
            + "\r\n - Error Code 50001 - Error in the backend database"
            + "\r\n - Error Code 50002 - Error in in the backend descriptor processor",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ), 
        @ApiResponse(
            responseCode = "501", 
            description = "[Bad Gateway](https://www.rfc-editor.org/rfc/rfc9110.html#name-502-bad-gateway)"
            + "\r\n - Error Code 50201 - Invalid policyService's response" 
            + "\r\n - Error Code 50204 - Invalid notificationService's response",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public String createDataProductVersionEndpoint(
        @Parameter(description = "The identifier of the data product to which the new version must be associated")
        @PathVariable String id,

        @Parameter(description = "A data product descriptor document compliant with [DPDS version 1.0.0-DRAFT](https://dpds.opendatamesh.org/resources/specifications/1.0.0-DRAFT/)")
        @RequestBody String descriptorContent
    )  {

       return createDataProductVersion(id, descriptorContent);
    }

    public abstract String createDataProductVersion(String id, String descriptorContent);


    // ======================================================================================
    // GET /products/{id}/versions
    // ======================================================================================

    @GetMapping(
        value = "/{id}/versions"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get all register definitions",
        description = "Get all definitions registered in the Data Product Registry"
    ) 
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "All register version numbers of the data product identified by the input `id`", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "[Bad request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
            + "\r\n - Error Code 40007 - Product id is empty",  
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n - Error Code 40401 - Data product not found",            
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
    public List<String> getDataProductVersionsEndpoint(
        @Parameter(description = "The denntifier of the data product")
        @PathVariable String id) 
    {
       return getDataProductVersions(id);
    }

    public abstract List<String> getDataProductVersions(String id);

    // ======================================================================================
    // GET /products/{id}/versions/{version}
    // ======================================================================================

    @GetMapping(
        value = "/{id}/versions/{version}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get the specified data product version",
        description = "Get the data product version `version` of the data product identified by `id`."
        + "The retuned value is a descriptor document compliant with [DPDS version 1.0.0-DRAFT](https://dpds.opendatamesh.org/resources/specifications/1.0.0-DRAFT/)."
    ) 
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The descriptor document that describe the spcified data product version", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DataProductVersionDPDS.class))
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
    public String getDataProductVersionEndpoint(
        @Parameter(description = "The identifier of the data product")
        @PathVariable(value = "id") String id,
        
        @Parameter(description = "The data product version number")
        @PathVariable(value = "version") String version,
        
        @Parameter(
            description = "Format used to serialize the descriptor document. Available formats are:" 
            + "\r\n - normalized (default): TODO" 
            + "\r\n - canonical: TODO"
        )
        @RequestParam(name = "format", required = false) String format
    ) {
       return getDataProductVersion(id, version, format);
    }

    public abstract String getDataProductVersion(String id, String version, String format);


    // ======================================================================================
    // DELETE /products/{id}/versions/{version}
    // ======================================================================================

    @DeleteMapping(
        value = "/{id}/versions/{version}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Delete the specified data product version",
        description = "Delete the data product version `version` of the data product identified by `id`"
    ) 
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Data product version deleted"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "[Bad request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
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
            + "\r\n - Error Code 50001 - Error in the backend database",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public void deleteDataProductVersionEndpoint(
        @PathVariable String id, 
        @PathVariable String version
    ) {
        deleteDataProductVersion(id, version);
    }


    public abstract void deleteDataProductVersion(String id, String version);
}
