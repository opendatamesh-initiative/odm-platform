package org.opendatamesh.platform.pp.registry.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DefinitionResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.v1.resources.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.TemplateDefinition;
import org.opendatamesh.platform.pp.registry.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.TemplateDefinitionMapper;
import org.opendatamesh.platform.pp.registry.services.TemplateDefinitionService;
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
    value =  "/templates", 
    produces = { 
        "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", 
        "application/json"
    }
)
@Validated
@Tag(
    name = "Templates", 
    description = "Tempates's definitions")
public class TemplateDefinitionController {

    @Autowired
    private TemplateDefinitionService templateDefinitionService;

    @Autowired
    private TemplateDefinitionMapper templateDefinitionMapper;

    private static final Logger logger = LoggerFactory.getLogger(TemplateDefinitionController.class);

    public TemplateDefinitionController() { 
        logger.debug("Standard Template definitions controller successfully started");
    }

    // ======================================================================================
    // DEFINITIONS
    // ======================================================================================
    
    // ----------------------------------------
    // CREATE Definition 
    // ----------------------------------------
   
    @PostMapping(
        consumes = { "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @Operation(
        summary = "Register the  Template definition",
        description = "Register the provided Template definition in the Data Product Registry" 
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Template definition registered", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DefinitionResource.class)
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
    public DefinitionResource createDefinition(
        @Parameter( 
            description = "An Template definition object", 
            required = true)
        @Valid @RequestBody(required=false)  DefinitionResource definitionRes
    ) {
        if(definitionRes == null) {
            throw new BadRequestException(
                OpenDataMeshAPIStandardError.SC400_14_TEMPLATE_IS_EMPTY,
                "Template definition cannot be empty");
        }
        
        TemplateDefinition templateDefinition = templateDefinitionMapper.toEntity(definitionRes);
        templateDefinition.setStatus("ACTIVE"); // TODO find a better way to manage read-only properties
        templateDefinition.setType("TEMPLATE"); // TODO find a better way to manage read-only properties
        templateDefinition = templateDefinitionService.createDefinition(templateDefinition);
        return templateDefinitionMapper.toResource(templateDefinition);
    }

    // ----------------------------------------
    // READ All definitions
    // ----------------------------------------

    @GetMapping
    @ResponseStatus(HttpStatus.OK) 
    @Operation(
        summary = "Get all registered Template definitions",
        description = "Get all Template definitions registered in the Data Product Registry."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The list of all registered Template definitions", 
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
    public List<DefinitionResource> readAllDefinitions(
        @Parameter(description="Add `name` parameter to the request to get only definitions with the specific name")
        @RequestParam(required = false, name = "name") 
        String name, 

        @Parameter(description="Add `version` parameter to the request to get only definitions with the specific version")
        @RequestParam(required = false, name = "version") 
        String version, 
        
        @Parameter(description="Add `type` parameter to the request to get only definitions with the specific type")
        @RequestParam(required = false, name = "type") 
        String type, 

        @Parameter(description="Add `specification` parameter to the request to get only definitions with the specific specification")
        @RequestParam(required = false, name = "specification") 
        String specification,

        @Parameter(description="Add `specificationVersion` parameter to the request to get only definitions with teh specific specification version")
        @RequestParam(required = false, name = "specificationVersion") 
        String specificationVersion
    )
    {
        List<TemplateDefinition> definitions = templateDefinitionService.searchDefinitions(name, version, type, specification, specificationVersion);
        List<DefinitionResource> definitionResources = templateDefinitionMapper.definitionsToResources(definitions);
        return definitionResources;
    }

    // ----------------------------------------
    // READ Definition
    // ----------------------------------------

    @GetMapping(
        value = "/{id}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get the specified Template definition",
        description = "Get the Template definition identified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested definition", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DefinitionResource.class)
            )
        ),
        @ApiResponse(
                responseCode = "404",
                description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                        + "\r\n - Error Code 40403 - Standard Definition not found",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public DefinitionResource readOneDefinition(
        @Parameter(description = "Idenntifier of the Template definition")
        @Valid @PathVariable(value = "id") Long id) 
    {
        TemplateDefinition definition = templateDefinitionService.readDefinition(id);
        DefinitionResource definitionResource = templateDefinitionMapper.toResource(definition);
        return definitionResource;
    }

    // ----------------------------------------
    // UPDATE Definition
    // ----------------------------------------

    // Definitions are immutable ojects, the cannot be updated after creations


    // ----------------------------------------
    // DELETE Definition
    // ----------------------------------------

     // TODO add all error responses

     @DeleteMapping(
        value = "/{id}"
     )
     @ResponseStatus(HttpStatus.OK)
     @Operation(
             summary = "Delete the specified Template definition",
             description = "Delete the Template definition identified by the input `id`"
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
    public void deleteDefinition(
        @Parameter(description = "Identifier of the definition")
        @PathVariable Long id
    )
    {
        templateDefinitionService.deleteDefinition(id);
    }

    
}
