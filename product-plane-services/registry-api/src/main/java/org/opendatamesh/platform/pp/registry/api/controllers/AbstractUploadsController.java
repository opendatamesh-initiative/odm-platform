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
import org.opendatamesh.platform.pp.registry.api.resources.DataProductDescriptorLocationResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// TODO verify if is it possible to expose only one endpoints and using 

@RestController
@RequestMapping(
    value =  "/products", 
    produces = { "application/vnd.odmp.v1+json", "application/vnd.odmp+json", "application/json"}
)
@Validated
@Tag(
    name = "Uploads", 
    description = "Uploads")
public abstract class AbstractUploadsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AbstractUploadsController.class);

    public AbstractUploadsController() { 
        logger.debug("Data product uploads controller successfully started");
    }

    // ======================================================================================
    // POST /uploads
    // ======================================================================================

    // here the media type is an uristring
    @PostMapping(
        value = "/uploads", 
        consumes = { "application/vnd.odmp.v1+json", "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @Operation(
        summary = "Upload a new data product version",
        description = "Upload a new data product version using the input descriptor referenced by the provided uri."
        + "Create also the data product specified in the descriptor document if it does not exist yet. "
        + "To create the new data product version only if the data product already exist use the endpoint `POST /products/{id}/versions`. "
        + "\r\n _Note: it is not possible to create a data product without any version associated. " 
        +" For this reason this endpoint creates the data product together with its first version. " 
        + "It can then be used also to create successive versions._" 
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "201", 
                description = "Data product version created", 
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = DataProductVersionDPDS.class)
                )
            ),
            @ApiResponse(
                responseCode = "400", 
                description = "[Bad request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
                + "\r\n - Error Code 40001 - Descriptor document is empty" 
                + "\r\n - Error Code 40002 - Version already exists",  
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                responseCode = "422", 
                description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                + "\r\n - Error Code 42201 - Descriptor URI is invalid"
                + "\r\n - Error Code 42202 - Descriptor document syntax is invalid"
                + "\r\n - Error Code 42203 - Descriptor document semantic is invalid",  
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                responseCode = "500", 
                description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
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
    public String uploadDataProductVersionEndpoint(
            @Parameter(description = "The fully qualified name of the data product to be created or updated with a new version")
            @RequestParam(value = "fqn", required = false) String fqn,
            @Parameter(description = "A data product descriptor source", required = true)
            @RequestBody(required=false) DataProductDescriptorLocationResource descriptorLocationRes
    ) {
       return uploadDataProductVersion(fqn, descriptorLocationRes);
    }

    public abstract String uploadDataProductVersion(String fqn, DataProductDescriptorLocationResource descriptorLocationRes);

}
