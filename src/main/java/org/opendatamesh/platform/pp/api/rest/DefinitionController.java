package org.opendatamesh.platform.pp.api.rest;

import java.util.List;

import javax.validation.Valid;

import org.opendatamesh.platform.pp.api.database.entities.sharedres.Definition;
import org.opendatamesh.platform.pp.api.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.api.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.api.resources.v1.ErrorRes;
import org.opendatamesh.platform.pp.api.resources.v1.mappers.DefinitionMapper;
import org.opendatamesh.platform.pp.api.resources.v1.sharedres.DefinitionResource;
import org.opendatamesh.platform.pp.api.services.DefinitionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    value =  "/definitions", 
    produces = { 
        "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", 
        "application/json"
    }
)
@Validated
@Tag(
    name = "Definitions", 
    description = "Definitions")
public class DefinitionController {

    @Autowired
    private DefinitionService definitionService;

    @Autowired
    private DefinitionMapper definitionMapper;

    private static final Logger logger = LoggerFactory.getLogger(DefinitionController.class);

    public DefinitionController() { 
        logger.debug("Standard definitions controller succesfully started");
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
        summary = "Register the  definition",
        description = "Register the provided definition in the Data Product Registry" 
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Definition registered", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DefinitionResource.class)
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
            + "\r\n - Error Code 42208 - Definition document is invalid"
            + "\r\n - Error Code 42209 - Definition already exists",
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
    public DefinitionResource createataDefinition( 
        @Parameter( 
            description = "A definition object", 
            required = true)
        @Valid @RequestBody(required=false)  Definition definition
    ) throws Exception {
        if(definition == null) {
            throw new BadRequestException(
                OpenDataMeshAPIStandardError.SC400_10_PRODUCT_IS_EMPTY,
                "Definition cannot be empty");
        }
        
        definition = definitionService.createDefinition(definition);
        return definitionMapper.toResource(definition);
    }

    // ----------------------------------------
    // READ All definitions
    // ----------------------------------------
    @GetMapping
    @ResponseStatus(HttpStatus.OK) 
    @Operation(
        summary = "Get all registered definitions",
        description = "Get all definitions registered in the Data Product Registry."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested definition", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DefinitionResource.class)
            )
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
        List<Definition> definitions = definitionService.searchDefinitions(name, version, type, specification, specificationVersion);
        List<DefinitionResource> definitionResources = definitionMapper.definitionsToResources(definitions);
        return definitionResources;
    }

    // ----------------------------------------
    // READ Definition
    // ----------------------------------------

    // TODO add all error responses

    @GetMapping(
        value = "/{id}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get the specified definition",
        description = "Get the definition identified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested definition", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DefinitionResource.class)
            )
        )
    })
    public DefinitionResource readOneDefinition(
        @Parameter(description = "Idenntifier of the data product")
        @Valid @PathVariable(value = "id") Long id) 
    {
        Definition definition = definitionService.readDefinition(id);
        DefinitionResource definitionResource = definitionMapper.toResource(definition);
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
        summary = "Delete the specified definition",
        description = "Delete the data product identified by the input `id` and all its associated versions"
        
    )
    public void deleteDefinition(
        @Parameter(description = "Identifier of the definition")
        @PathVariable Long id
    )
    {
        definitionService.deleteDefinition(id);
    }

    
}
