package org.opendatamesh.platform.pp.registry.rest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Template;
import org.opendatamesh.platform.pp.registry.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.TemplateMapper;
import org.opendatamesh.platform.pp.registry.resources.v1.shared.TemplateResource;
import org.opendatamesh.platform.pp.registry.services.TemplateService;
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
        description = "Templates")
public class TemplateController {

    @Autowired
    private TemplateService templateService;

    @Autowired
    private TemplateMapper templateMapper;

    private static final Logger logger = LoggerFactory.getLogger(TemplateController.class);

    public TemplateController() {
        logger.debug("Templates controller successfully started");
    }

    // ======================================================================================
    // TEMPLATES
    // ======================================================================================

    // ----------------------------------------
    // CREATE Template 
    // ----------------------------------------

    @PostMapping(
            consumes = { "application/vnd.odmp.v1+json",
                    "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Register the  template",
            description = "Register the provided template in the Data Product Registry"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Template registered",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TemplateResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                            + "\r\n - Error Code 40012 - Template is empty",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42210 - Template already exists"
                            + "\r\n - Error Code 42211 - Template document is invalid",
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
    public TemplateResource createTemplate(
            @Parameter(
                    description = "A template object",
                    required = true)
            @Valid @RequestBody(required=false) TemplateResource templateResource
    ) {
        if(templateResource == null) {
            throw new BadRequestException(
                    OpenDataMeshAPIStandardError.SC400_12_TEMPLATE_IS_EMPTY,
                    "Template cannot be empty");
        }

        Template template = templateMapper.toEntity(templateResource);
        template = templateService.createTemplate(template);
        return templateMapper.toResource(template);
    }

    // ----------------------------------------
    // READ All templates
    // ----------------------------------------

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get all registered templates",
            description = "Get all templates registered in the Data Product Registry."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested template",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TemplateResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50001 - Error in in the backend database",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    public List<TemplateResource> readAllTemplates(
            @Parameter(description="Add `mediaType` parameter to the request to get only templates with the specific mediaType")
            @RequestParam(required = false, name = "mediaType")
            String mediaType
    )
    {
        List<Template> templates = templateService.searchTemplates(mediaType);
        List<TemplateResource> templateResources = templateMapper.templatesToResources(templates);
        return templateResources;
    }

    // ----------------------------------------
    // READ Template
    // ----------------------------------------

    @GetMapping(
            value = "/{id}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Get the specified template",
            description = "Get the template identified by the input `id`"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "The requested template",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = TemplateResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40404 - Template not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    public TemplateResource readOneTemplate(
            @Parameter(description = "Identifier of the template")
            @Valid @PathVariable(value = "id") Long id)
    {
        Template template = templateService.readTemplate(id);
        TemplateResource templateResource = templateMapper.toResource(template);
        return templateResource;
    }

    // ----------------------------------------
    // UPDATE Template
    // ----------------------------------------

    // Templates are immutable ojects, the cannot be updated after creations


    // ----------------------------------------
    // DELETE Template
    // ----------------------------------------

    @DeleteMapping(
            value = "/{id}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
            summary = "Delete the specified template",
            description = "Delete the data product identified by the input `id` and all its associated versions"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40404 - Template not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                            + "\r\n - Error Code 50001 - Error in in the backend database",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    public void deleteTemplate(
            @Parameter(description = "Identifier of the template")
            @PathVariable Long id
    )
    {
        templateService.deleteTemplate(id);
    }
    
}
