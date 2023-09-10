package org.opendatamesh.platform.pp.registry.server.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.api.resources.ApiToSchemaRelationshipResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.api.resources.SchemaResource;
import org.opendatamesh.platform.pp.registry.server.database.entities.ApiToSchemaRelationship;
import org.opendatamesh.platform.pp.registry.server.database.mappers.SchemaMapper;
import org.opendatamesh.platform.pp.registry.server.services.SchemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
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
@Validated
@Tag(
    name = "Schemas", 
    description = "Schemas")
public class SchemaController {

    @Autowired
    private SchemaService schemaService;

    @Autowired
    private SchemaMapper schemaMapper;

    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);

    public SchemaController() { 
        logger.debug("Schemas controller succesfully started");
    }

    // ======================================================================================
    // SCHEMAS
    // ======================================================================================
    
    // ----------------------------------------
    // CREATE Schema 
    // ----------------------------------------

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
    public SchemaResource createSchema(
        @Parameter( 
            description = "A schema object", 
            required = true)
        @Valid @RequestBody(required=false)  SchemaResource schemaResource
    ) {
        if(schemaResource == null) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_12_SCHEMA_IS_EMPTY,
                "Schema cannot be empty");
        }

        org.opendatamesh.platform.pp.registry.server.database.entities.Schema schema = schemaMapper.toEntity(schemaResource);
        schema = schemaService.createSchema(schema);
        return schemaMapper.toResource(schema);
    }

    // ----------------------------------------
    // READ All definitions
    // ----------------------------------------

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
    public List<SchemaResource> readAllSchemas(
        @Parameter(description="Add `name` parameter to the request to get only schemas with the specific name")
        @RequestParam(required = false, name = "name") 
        String name, 

        @Parameter(description="Add `version` parameter to the request to get only schemas with the specific version")
        @RequestParam(required = false, name = "version") 
        String version,

        @Parameter(description="Add `apiId` parameter to the request to get only schemas related to the specific api")
        @RequestParam(required = false, name = "apiId") 
        String apiId,

        @Parameter(description="Add `content` parameter in request to specify to include or not raw content in response. The defualt is false")
        @RequestParam(required = false, defaultValue = "false", name = "content") 
        boolean includeContent
    )
    {
        List<org.opendatamesh.platform.pp.registry.server.database.entities.Schema> schemas;
        schemas = schemaService.searchSchemas(apiId, name, version);
        List<SchemaResource> schemaResources = schemaMapper.schemasToResources(schemas);
        if(includeContent == false) {
            for(SchemaResource schemaResource: schemaResources) {
                schemaResource.setContent(null);
            }
        }
        return schemaResources;
    }

    // ----------------------------------------
    // READ Schema
    // ----------------------------------------

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
    public SchemaResource readOneSchema(
        @Parameter(description = "Idenntifier of the schema")
        @Valid @PathVariable(value = "id") Long id) 
    {
        org.opendatamesh.platform.pp.registry.server.database.entities.Schema schema;
        schema = schemaService.readSchema(id);
        SchemaResource schemaResource = schemaMapper.toResource(schema);
        return schemaResource;
    }

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
    public String readSchemaContent(
        @Parameter(description = "Idenntifier of the schema")
        @Valid @PathVariable(value = "id") Long id) 
    {
        org.opendatamesh.platform.pp.registry.server.database.entities.Schema schema;
        schema = schemaService.readSchema(id);
        return schema.getContent();
    }

    // ----------------------------------------
    // READ Schema to API relationships
    // ----------------------------------------

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
    public List<ApiToSchemaRelationshipResource> readSchemaRelationships(
        @Parameter(description = "Idenntifier of the schema")
        @Valid @PathVariable(value = "id") Long id) 
    {
        List<ApiToSchemaRelationship> relationships = schemaService.readSchemaRealtionships(id); // just to check that the schema exists
        return schemaMapper.relationshipsToResources(relationships);
    }

    // ----------------------------------------
    // UPDATE Schema
    // ----------------------------------------

    // Schemas are immutable ojects, the cannot be updated after creations


    // ----------------------------------------
    // DELETE Schema
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
    public void deleteSchema(
        @Parameter(description = "Identifier of the schema")
        @PathVariable Long id
    )
    {
        schemaService.deleteSchema(id);
    }

}
