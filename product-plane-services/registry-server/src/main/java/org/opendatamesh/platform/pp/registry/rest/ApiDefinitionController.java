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
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.ApiDefinition;
import org.opendatamesh.platform.pp.registry.api.v1.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.api.v1.exceptions.ODMRegistryAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.ApiDefinitionMapper;
import org.opendatamesh.platform.pp.registry.services.ApiDefinitionService;
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
public class ApiDefinitionController {

    @Autowired
    private ApiDefinitionService apiDefinitionService;

    @Autowired
    private ApiDefinitionMapper definitionMapper;

    private static final Logger logger = LoggerFactory.getLogger(ApiDefinitionController.class);

    public ApiDefinitionController() { 
        logger.debug("Standard api definitions controller successfully started");
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
        summary = "Register the  API definition",
        description = "Register the provided API definition in the Data Product Registry" 
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "API definition registered", 
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
            description = "An API definition object", 
            required = true)
        @Valid @RequestBody(required=false)  DefinitionResource definition
    ) {
        if(definition == null) {
            throw new BadRequestException(
                ODMRegistryAPIStandardError.SC400_08_STDDEF_IS_EMPTY,
                "API definition cannot be empty");
        }
        
        ApiDefinition apiDefinition = definitionMapper.toEntity(definition);
        apiDefinition = apiDefinitionService.createDefinition(apiDefinition);
        return definitionMapper.toResource(apiDefinition);
    }

    // ----------------------------------------
    // READ All definitions
    // ----------------------------------------

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
        List<ApiDefinition> definitions = apiDefinitionService.searchDefinitions(name, version, type, specification, specificationVersion);
        List<DefinitionResource> definitionResources = definitionMapper.definitionsToResources(definitions);
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
        summary = "Get the specified API definition",
        description = "Get the API definition identified by the input `id`"
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
        @Parameter(description = "Idenntifier of the API definition")
        @Valid @PathVariable(value = "id") Long id) 
    {
        ApiDefinition apiDefinition = apiDefinitionService.readDefinition(id);
        return definitionMapper.toResource(apiDefinition);
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
    public void deleteDefinition(
        @Parameter(description = "Identifier of the definition")
        @PathVariable Long id
    )
    {
        apiDefinitionService.deleteDefinition(id);
    }

    
}
