package org.opendatamesh.platform.pp.registry.rest;

import java.net.URI;
import java.net.URISyntaxException;

import javax.validation.Valid;

import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.parser.location.DescriptorLocation;
import org.opendatamesh.platform.core.dpds.parser.location.GitLocation;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.core.dpds.serde.DataProductVersionSerializer;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProductVersion;
import org.opendatamesh.platform.pp.registry.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.DataProductDescriptorLocationResource;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DataProductMapper;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.services.DataProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
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
    private DataProductVersionMapper dataProductVersionMapper;


    @Autowired
    ObjectMapper objectMapper;
    
    private static final Logger logger = LoggerFactory.getLogger(DataProductUploadsController.class);

    public DataProductUploadsController() { 
        logger.debug("Data product uploads controller succesfully started");
    }



    // here the media type is an uristring
    @PostMapping(
        value = "/uploads", 
        consumes = { "application/vnd.odmp.v1+json", "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @Operation(
        summary = "Upload a new data product version",
        description = "Upload a new data product version using the input descriptor referenced by the rpovided uri."
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
                + "\r\n - Error Code 50204 - Invalid metaService's response",
                content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            )
    })
    public String createVersionFromURI(
        @Parameter( 
            description = "A data product descriptor source", 
            required = true)
        @Valid @RequestBody(required=false)  DataProductDescriptorLocationResource descriptorLocationRes
    ) {
        DescriptorLocation descriptorLocation = null;
        try {
            URI descriptorUri = new URI(descriptorLocationRes.getRootDocumentUri());
            if(descriptorLocationRes.getGit() != null 
                && StringUtils.hasText(descriptorLocationRes.getGit().getRepositorySshUri())) {
                descriptorLocation = new GitLocation(
                    descriptorLocationRes.getGit().getRepositorySshUri(), descriptorUri,
                    descriptorLocationRes.getGit().getBranch(), descriptorLocationRes.getGit().getTag()
                );
            } else {
                descriptorLocation = new UriLocation(descriptorUri);
            }

        } catch (URISyntaxException e) {
            throw new BadRequestException(
                OpenDataMeshAPIStandardError.SC400_05_INVALID_URILIST,
                "Provided URI is invalid [" + descriptorLocationRes.getRootDocumentUri() + "]", e);
        }
        String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        DataProductVersion dataProductVersion = dataProductService.addDataProductVersion(descriptorLocation, true, serverUrl);
        DataProductVersionDPDS dataProductVersionDPDS = dataProductVersionMapper.toResource(dataProductVersion);
        DataProductVersionSerializer serializer = new DataProductVersionSerializer();
        String serailizedContent = null;
        try {
            serailizedContent = serializer.serialize(dataProductVersionDPDS, "canonical", "json", true);
        } catch (JsonProcessingException e) {
           throw new InternalServerException(
            OpenDataMeshAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
            "Impossible to serialize data product version raw content", e);
        }
        return serailizedContent;
    }

    /*
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
    */

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
