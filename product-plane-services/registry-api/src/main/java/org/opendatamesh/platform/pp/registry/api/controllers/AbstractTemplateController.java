package org.opendatamesh.platform.pp.registry.api.controllers;

import java.util.List;

import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
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
@RequestMapping(value = "/templates", produces = {
        "application/vnd.odmp.v1+json",
        "application/vnd.odmp+json",
        "application/json"
})
@Tag(name = "Templates", description = "Tempates's definitions")
public abstract class AbstractTemplateController {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTemplateController.class);

    public AbstractTemplateController() {
        logger.debug("Template controller successfully started");
    }

    // @see
    // https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations#arrayschema

    // ===============================================================================
    // POST /templates
    // ===============================================================================

    @PostMapping(consumes = { "application/vnd.odmp.v1+json",
            "application/vnd.odmp+json", "application/json" })
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Register the  Template definition", description = "Register the provided Template definition in the Data Product Registry")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Template definition registered", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExternalComponentResource.class))),
            @ApiResponse(responseCode = "400", description = "[Bad Request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                    + "\r\n - Error Code 40008 - Standard Definition is empty", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class)) }),
            @ApiResponse(responseCode = "422", description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                    + "\r\n - Error Code 42206 - Definition already exists"
                    + "\r\n - Error Code 42208 - Definition document is invalid", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class)) }),
            @ApiResponse(responseCode = "500", description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                    + "\r\n - Error Code 50000 - Error in the backend service"
                    + "\r\n - Error Code 50001 - Error in in the backend database", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class)) })
    })
    public ExternalComponentResource createTemplateEndpoint(
            @Parameter(description = "An Template definition object", required = true) @RequestBody(required = false) ExternalComponentResource definitionRes) {

        return createTemplate(definitionRes);
    }

    public abstract ExternalComponentResource createTemplate(ExternalComponentResource definitionRes);

    // ===============================================================================
    // GET /templates
    // ===============================================================================

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all registered Template definitions", description = "Get all Template definitions registered in the Data Product Registry.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The list of all registered Template definitions", content = @Content(mediaType = "application/json", schema = @Schema(implementation = List.class))),
            @ApiResponse(responseCode = "500", description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                    + "\r\n - Error Code 50001 - Error in in the backend database", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class)) })
    })
    public List<ExternalComponentResource> getTemplatesEndpoint(
            @Parameter(description = "Add `name` parameter to the request to get only definitions with the specific name") @RequestParam(required = false, name = "name") String name,

            @Parameter(description = "Add `version` parameter to the request to get only definitions with the specific version") @RequestParam(required = false, name = "version") String version,

            @Parameter(description = "Add `specification` parameter to the request to get only definitions with the specific specification") @RequestParam(required = false, name = "specification") String specification,

            @Parameter(description = "Add `specificationVersion` parameter to the request to get only definitions with teh specific specification version") @RequestParam(required = false, name = "specificationVersion") String specificationVersion) {
        return getTemplates(name, version, specification, specificationVersion);
    }

    public abstract List<ExternalComponentResource> getTemplates(
            String name, String version,
            String specification, String specificationVersion);

    // ===============================================================================
    // GET /templates/{id}
    // ===============================================================================

    @GetMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get the specified Template definition", description = "Get the Template definition identified by the input `id`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The requested definition", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ExternalComponentResource.class))),
            @ApiResponse(responseCode = "404", description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                    + "\r\n - Error Code 40403 - Standard Definition not found", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class)) })
    })
    public ExternalComponentResource getTemplateEndpoint(
            @Parameter(description = "Idenntifier of the Template definition") @PathVariable(value = "id") String id) {
    
        return getTemplate(id);
    }

    public abstract ExternalComponentResource getTemplate( String id);

    // ===============================================================================
    // UPDATE /templates/{id}
    // ===============================================================================

    // Templates are immutable ojects, the cannot be updated after creations

    // ===============================================================================
    // DELETE /templates/{id}
    // ===============================================================================

    // TODO add all error responses

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Delete the specified Template definition", description = "Delete the Template definition identified by the input `id`")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "404", description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                    + "\r\n - Error Code 40403 - Standard Definition not found", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class)) }),
            @ApiResponse(responseCode = "500", description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
                    + "\r\n - Error Code 50001 - Error in in the backend database", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class)) })
    })
    public void deleteTemplateEndpoint(
            @Parameter(description = "Identifier of the definition") @PathVariable String id) {
        deleteTemplate(id);
    }

    public abstract void deleteTemplate(String id);

}
