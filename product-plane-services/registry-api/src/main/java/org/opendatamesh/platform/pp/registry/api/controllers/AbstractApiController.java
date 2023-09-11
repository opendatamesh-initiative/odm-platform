package org.opendatamesh.platform.pp.registry.api.controllers;

import java.util.List;

import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    value =  "/apis", 
    produces = { 
        "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", 
        "application/json"
    }
)
@Validated
@Tag(
    name = "APIs", 
    description = "API's definitions")
public abstract class AbstractApiController {

    
    private static final Logger logger = LoggerFactory.getLogger(AbstractApiController.class);

    public AbstractApiController() { 
        logger.debug("Standard api definitions controller successfully started");
    }

    // @see https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations#arrayschema

    // ===============================================================================
    // POST /apis  
    // ===============================================================================
   
    @PostMapping(
        consumes = { "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @Operation(
        summary = "Register the  API definition",
        description = "Register the provided API definition in the Data Product Registry" 
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "API definition registered", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = ExternalComponentResource.class)
            )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                        + "\r\n - Error Code 40008 - Standard Definition is empty",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
            + "\r\n - Error Code 42206 - Definition already exists"
            + "\r\n - Error Code 42208 - Definition document is invalid",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
         @ApiResponse(
            responseCode = "500", 
            description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
            + "\r\n - Error Code 50000 - Error in the backend service"
            + "\r\n - Error Code 50001 - Error in in the backend database",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public ExternalComponentResource createApiEndpoint(
        @Parameter( 
            description = "An API definition object", 
            required = true)
        @RequestBody(required=false)  ExternalComponentResource apiRes
    ) {
        return createApi(apiRes);
    }

    public abstract  ExternalComponentResource createApi(ExternalComponentResource apiRes);

    // ===============================================================================
    // GET /apis
    // ===============================================================================

    @GetMapping
    @ResponseStatus(HttpStatus.OK) 
    @Operation(
        summary = "Get all registered API definitions",
        description = "Get all API definitions registered in the Data Product Registry."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The list of all registered API definitions", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = List.class)
            )
        ),
        @ApiResponse(
                responseCode = "500",
                description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                        + "\r\n - Error Code 50001 - Error in in the backend database",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public List<ExternalComponentResource> getApisEndpoint(
        @Parameter(description="Add `name` parameter to the request to get only definitions with the specific name")
        @RequestParam(required = false, name = "name") 
        String name, 

        @Parameter(description="Add `version` parameter to the request to get only definitions with the specific version")
        @RequestParam(required = false, name = "version") 
        String version, 
        
        @Parameter(description="Add `specification` parameter to the request to get only definitions with the specific specification")
        @RequestParam(required = false, name = "specification") 
        String specification,

        @Parameter(description="Add `specificationVersion` parameter to the request to get only definitions with teh specific specification version")
        @RequestParam(required = false, name = "specificationVersion") 
        String specificationVersion
    )
    {
        return getApis(name, version, specification, specificationVersion);
    }

    public abstract List<ExternalComponentResource> getApis(
        String name, String version, 
        String specification,String specificationVersion);
    
    // ===============================================================================
    // GET /apis/{id}
    // ===============================================================================

    @GetMapping(
        value = "/{id}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get the specified API definition",
        description = "Get the API definition identified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested definition", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = ExternalComponentResource.class)
            )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                        + "\r\n - Error Code 40403 - Standard Definition not found",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public ExternalComponentResource getApiEndpoint(
        @Parameter(description = "Identifier of the API definition")
        @PathVariable(value = "id") String id) 
    {
        return getApi(id);
    }

    public abstract ExternalComponentResource getApi(String id);

    // ----------------------------------------
    // UPDATE Definition
    // ----------------------------------------

    // Api are immutable ojects, they cannot be updated after creations


    // ===============================================================================
    // DELETE /products/{id}
    // ===============================================================================
    

    // TODO add all error responses

    

    @DeleteMapping(
        value = "/{id}"
     )
     @ResponseStatus(HttpStatus.OK)
     @Operation(
             summary = "Delete the specified API definition",
             description = "Delete the API definition identified by the input `id`"
     )
     @ApiResponses(value = {
             @ApiResponse(
                     responseCode = "404",
                     description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                             + "\r\n - Error Code 40403 - Standard Definition not found",
                     content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
             ),
             @ApiResponse(
                     responseCode = "500",
                     description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                             + "\r\n - Error Code 50001 - Error in in the backend database",
                     content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
             )
     })
    public void deleteApiEndpoint(
        @Parameter(description = "Identifier of the definition")
        @PathVariable String id
    )
    {
        deleteApi(id);
    }

    public abstract void deleteApi(String id);
}
