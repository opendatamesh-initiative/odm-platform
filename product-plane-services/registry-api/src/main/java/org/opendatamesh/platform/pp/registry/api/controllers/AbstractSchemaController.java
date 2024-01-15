package org.opendatamesh.platform.pp.registry.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.resources.ApiToSchemaRelationshipResource;
import org.opendatamesh.platform.pp.registry.api.resources.SchemaResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
    value =  "/schemas", 
    produces = { 
        "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", 
        "application/json"
    }
)
@Tag(
    name = "Schemas", 
    description = "Schemas")
public abstract class AbstractSchemaController {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSchemaController.class);

    public AbstractSchemaController() { 
        logger.debug("Schemas controller successfully started");
    }

    // @see https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations#arrayschema

    // ===============================================================================
    // POST /schemas  
    // ===============================================================================
   

    @PostMapping(
        consumes = { "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", "application/json"},
        produces = {"application/json", "application/yaml", "text/plain"}
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @Operation(
        summary = "Register the  schema",
        description = "Register the provided schema in the Data Product Registry" 
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Schema registered", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = SchemaResource.class)
            )
        ),
        @ApiResponse(
                responseCode = "400",
                description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                        + "\r\n - Error Code 40012 - Schema is empty",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
            + "\r\n - Error Code 42210 - Schema is invalid"
            + "\r\n - Error Code 42211 - Schema already exists",
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
    public SchemaResource createSchemaEndpoint(
        @Parameter( 
            description = "A schema object", 
            required = true)
         @RequestBody(required=false)  SchemaResource schemaRes
    ) {
       return createSchema(schemaRes);
    }

    public abstract SchemaResource createSchema(SchemaResource schemaRes);

    // ===============================================================================
    // GET /schemas
    // ===============================================================================

    @GetMapping
    @ResponseStatus(HttpStatus.OK) 
    @Operation(
        summary = "Get all registered schemas",
        description = "Get all schemas registered in the Data Product Registry."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The list of all registered schemas", 
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
    public List<SchemaResource> getSchemasEndpoint(
        @Parameter(description="Add `name` parameter to the request to get only schemas with the specific name")
        @RequestParam(required = false, name = "name") 
        String name, 

        @Parameter(description="Add `version` parameter to the request to get only schemas with the specific version")
        @RequestParam(required = false, name = "version") 
        String version,

        @Parameter(description="Add `apiId` parameter to the request to get only schemas related to the specific api")
        @RequestParam(required = false, name = "apiId") 
        String apiId,

        @Parameter(description="Add `content` parameter in request to specify to include or not raw content in response. The default is false")
        @RequestParam(required = false, defaultValue = "false", name = "content") 
        boolean includeContent
    )
    {
       return getSchemas(name, version, apiId, includeContent);
    }

    public abstract List<SchemaResource> getSchemas(String name, String version, String apiId, boolean includeContent);

    // ===============================================================================
    // GET /schemas/{id}
    // ===============================================================================

    @GetMapping(
        value = "/{id}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get the specified schema",
        description = "Get the schema identified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested schema", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = SchemaResource.class)
            )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                        + "\r\n - Error Code 40404 - Schema not found",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public SchemaResource getSchemaEndpoint(
        @Parameter(description = "Identifier of the schema")
        @PathVariable(value = "id") Long id) 
    {
       return getSchema(id);
    }

    public abstract SchemaResource getSchema(Long id) ;

    // ===============================================================================
    // GET /schemas/{id}/raw
    // ===============================================================================

    @GetMapping(
        value = "/{id}/raw",
        produces="text/plain"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get the specified schema raw content",
        description = "Get the schema raw content identified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested schema raw content", 
            content = @Content(
                mediaType = "text/plain", 
                schema = @Schema(implementation = String.class)
            )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                        + "\r\n - Error Code 40404 - Schema not found",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public String getSchemaContentEndpoint(
        @Parameter(description = "Identifier of the schema")
        @PathVariable(value = "id") Long id) 
    {
       return getSchemaContent(id);
    }

    public abstract String getSchemaContent(Long id) ;

    // ===============================================================================
    // GET /schemas/{id}/apis
    // ===============================================================================

    @GetMapping(
        value = "/{id}/apis",
        produces="application/json"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get the api that use the specified schema",
        description = "Get the api that use the schema specified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The list of all apis that use the specified schema", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = String.class)
            )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                        + "\r\n - Error Code 40404 - Schema not found",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public List<ApiToSchemaRelationshipResource> getSchemaRelationshipsEndpoint(
        @Parameter(description = "Identifier of the schema")
        @PathVariable(value = "id") Long id) 
    {
        return getSchemaRelationships(id);
    }

    public abstract List<ApiToSchemaRelationshipResource> getSchemaRelationships(Long id);
    
    // ----------------------------------------
    // UPDATE /schemas/{id}
    // ----------------------------------------

    // Schemas are immutable objects, they cannot be updated after creations


    // ----------------------------------------
    // DELETE /schemas/{id}
    // ----------------------------------------

    // TODO add all error responses

    @DeleteMapping(
        value = "/{id}"
     )
     @ResponseStatus(HttpStatus.OK)
     @Operation(
             summary = "Delete the specified schema",
             description = "Delete the schema identified by the input `id`"
     )
     @ApiResponses(value = {
             @ApiResponse(
                     responseCode = "404",
                     description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                             + "\r\n - Error Code 40404 - Schema not found",
                     content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
             ),
             @ApiResponse(
                     responseCode = "500",
                     description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                             + "\r\n - Error Code 50001 - Error in in the backend database",
                     content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
             )
     })
    public void deleteSchemaEndpoint(
        @Parameter(description = "Identifier of the schema")
        @PathVariable Long id
    )
    {
        deleteSchema(id);
    }

    public abstract void deleteSchema(Long id);

}
