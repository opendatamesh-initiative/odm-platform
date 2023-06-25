package org.opendatamesh.platform.pp.registry.rest;

import java.util.List;

import javax.validation.Valid;

import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Definition;
import org.opendatamesh.platform.pp.registry.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.DefinitionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.opendatamesh.platform.pp.registry.resources.v1.SchemaResource;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.SchemaMapper;
import org.opendatamesh.platform.pp.registry.services.SchemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
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

    private static final Logger logger = LoggerFactory.getLogger(DefinitionController.class);

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
        @Valid @RequestBody(required=false)  org.opendatamesh.platform.pp.registry.database.entities.sharedres.Schema schema
    ) {
        if(schema == null) {
            throw new BadRequestException(
                OpenDataMeshAPIStandardError.SC400_12_SCHEMA_IS_EMPTY,
                "Schema cannot be empty");
        }
        
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
        @Parameter(description="Add `name` parameter to the request to get only definitions with the specific name")
        @RequestParam(required = false, name = "name") 
        String name, 

        @Parameter(description="Add `version` parameter to the request to get only definitions with the specific version")
        @RequestParam(required = false, name = "version") 
        String version,

        @Parameter(description="Add `content` parameter in request to specify to include or not raw content in response. The defualt is false")
        @RequestParam(required = false, defaultValue = "false", name = "content") 
        boolean includeContent
    )
    {
        List<org.opendatamesh.platform.pp.registry.database.entities.sharedres.Schema> schemas;
        schemas = schemaService.searchSchemas(name, version);
        List<SchemaResource> schemaResources = schemaMapper.schemasToResources(schemas);
        if(includeContent == false) {
            for(SchemaResource schemaResource: schemaResources) {
                schemaResource.setContent(null);
            }
        }
        return schemaResources;
    }

    // ----------------------------------------
    // READ Definition
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
        org.opendatamesh.platform.pp.registry.database.entities.sharedres.Schema schema;
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
    public String readOneSchemaContent(
        @Parameter(description = "Idenntifier of the schema")
        @Valid @PathVariable(value = "id") Long id) 
    {
        org.opendatamesh.platform.pp.registry.database.entities.sharedres.Schema schema;
        schema = schemaService.readSchema(id);
        return schema.getContent();
    }

}
