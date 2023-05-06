package org.opendatamesh.platform.pp.registry.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.Valid;

import org.opendatamesh.platform.pp.registry.core.DataProductDescriptorValidator;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProductVersion;
import org.opendatamesh.platform.pp.registry.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DataProductMapper;
import org.opendatamesh.platform.pp.registry.services.DataProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
public class DataProductUploadsController 
{
    @Autowired
    private DataProductService dataProductService;

    @Autowired
    private DataProductMapper dataProductMapper;

    @Autowired
    DataProductDescriptorValidator dataProductDescriptorValidator;

    @Autowired
    ObjectMapper objectMapper;
    
    private static final Logger logger = LoggerFactory.getLogger(DataProductUploadsController.class);

    public DataProductUploadsController() { 
        logger.debug("Data product uploads controller succesfully started");
    }

    @PostMapping(
        value = "/uploads", 
        consumes = { "application/vnd.odmp.v1+json", "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @Operation(
        summary = "Upload a new data product version",
        description = "Upload a new data product version using the input descriptor document. "
        + "Create also the data product specified in the descriptor document if it does not exist yet. "
        + "To create the new data product version only if the data product already exist use the endpoint `POST /products/{id}/versions`. "
        + "\r\n _Note: it is not possible to create a data product without any version associated. " 
        +" For this reason this endpoint creates the data product together with its first version. " 
        + "It can then be used also to create successive versions._" 
        //, tags = { "Uploads" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "201", 
                description = "Data product version created", 
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = DataProductVersionResource.class)
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
                + "\r\n - Error Code 42202 - Descriptor document syntax is invalid"
                + "\r\n - Error Code 42203 - Descriptor document semantyc is invalid",  
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
                + "\r\n - Error Code 50204 - Invalid metaService's response",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    public String createVersionFromDocument(
        @Parameter( 
            description = "A data product descriptor document compliant with DPDS version 1.0.0-DRAFT", 
            required = true)
        @Valid @RequestBody(required=false)  String descriptorContent) 
    {
        if(!StringUtils.hasText(descriptorContent)) {
            throw new BadRequestException(
                OpenDataMeshAPIStandardError.SC400_01_DESCRIPTOR_IS_EMPTY,
                "Input descriptor document cannot be empty");
        }
        String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        DataProductVersion dataProductVersion = dataProductService.addDataProductVersion(descriptorContent, true, serverUrl);
        DataProductVersionResource dataProductVersionResource = dataProductMapper.toResource(dataProductVersion);
        return dataProductVersionResource.getRawContent(false);
    }


    // here the media type is an uristring
    @PutMapping(
        value = "/uploads", 
        consumes = { "application/vnd.odmp.v1+json", "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @Operation(
        summary = "Upload a new data product version",
        description = "Upload a new data product version using the input descriptor document. "
        + "Create also the data product specified in the descriptor document if it does not exist yet. "
        + "To create the new data product version only if the data product already exist use the endpoint `POST /products/{id}/versions`. "
        + "\r\n _Note: it is not possible to create a data product without any version associated. " 
        +" For this reason this endpoint creates the data product together with its first version. " 
        + "It can then be used also to create successive versions._" 
        //, tags = { "Uploads" }
    )
    @ApiResponses(value = {
            @ApiResponse(
                responseCode = "201", 
                description = "Data product version created", 
                content = @Content(
                    mediaType = "application/json", 
                    schema = @Schema(implementation = DataProductVersionResource.class)
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
                + "\r\n - Error Code 42202 - Descriptor document syntax is invalid"
                + "\r\n - Error Code 42203 - Descriptor document semantyc is invalid",  
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
                + "\r\n - Error Code 50204 - Invalid metaService's response",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    public String createVersionFromURI(
        @Parameter(
            description="An URI pointing to a fatchable data product descriptor document", 
            required = true,  
            content = @Content(mediaType = "text/uri-list"))
        @Valid @RequestBody String uriListString) 
    {
        URI descriptorUri = getUriFromUriListString(uriListString);
        String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        DataProductVersion dataProductVersion = dataProductService.addDataProductVersion(descriptorUri, true, serverUrl);
        DataProductVersionResource dataProductVersionResource = dataProductMapper.toResource(dataProductVersion);
        return dataProductVersionResource.getRawContent(false);
    }

    // ----------------
    // Private methods
    // ----------------

    // @see https://www.iana.org/assignments/media-types/text/uri-list
    private URI getUriFromUriListString(String uriListString) {
        
        String descriptorUriString = null;
        if(!StringUtils.hasText(uriListString) ) {
            throw new BadRequestException(
                OpenDataMeshAPIStandardError.SC400_05_INVALID_URILIST,
                "Request body is empty");
        } 
        String[] uriList = uriListString.split("\r\n");
        for(String uri: uriList) {
            if(uri.trim().startsWith("#")) continue;
            else {descriptorUriString = uri.trim(); break;}
        }
        if(descriptorUriString == null) {
            throw new BadRequestException(
                OpenDataMeshAPIStandardError.SC400_05_INVALID_URILIST,
                "Request body does not contain an URI");
        }
        
        URI descriptorUri = null;
        try {
            descriptorUri = new URI(uriListString);
        } catch (URISyntaxException e) {
            throw new BadRequestException(
                OpenDataMeshAPIStandardError.SC400_05_INVALID_URILIST,
                "Provided URI is invalid [" + uriListString + "]", e);
        }

        return descriptorUri;
    }
}
