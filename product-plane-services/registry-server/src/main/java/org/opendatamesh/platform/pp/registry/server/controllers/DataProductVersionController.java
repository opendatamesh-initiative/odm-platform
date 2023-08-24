package org.opendatamesh.platform.pp.registry.server.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.core.commons.servers.exceptions.NotFoundException;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.parser.location.UriLocation;
import org.opendatamesh.platform.core.dpds.serde.DataProductVersionSerializer;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct.DataProductVersion;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DataProductVersionMapper;
import org.opendatamesh.platform.pp.registry.server.services.DataProductService;
import org.opendatamesh.platform.pp.registry.server.services.DataProductVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(
    value =  "/products", 
    produces = { "application/vnd.odmp.v1+json", 
                 "application/vnd.odmp+json", 
                 "application/json"}
)
@Validated
@Tag(
    name = "Data Product Versions", 
    description = "Data Product Versions")
public class DataProductVersionController 
{

    @Autowired
    private DataProductService dataProductService;

    @Autowired
    private DataProductVersionService dataProductVersionService;

    @Autowired
    private DataProductVersionMapper dataProductVersionMapper;

    @Autowired
    ObjectMapper objectMapper;

    
    private static final Logger logger = LoggerFactory.getLogger(DataProductVersionController.class);

    public DataProductVersionController() { 
        logger.debug("Data product version controller succesfully started");
    }


    // ======================================================================================
    // VERSIONS
    // ======================================================================================
    
    // ----------------------------------------
    // CREATE ~ Data Product Version
    // ----------------------------------------

    @PostMapping(
        value = "/{id}/versions", 
        consumes = {"application/vnd.odmp.v1+json", 
                    "application/vnd.odmp+json", 
                    "application/json"
        }
    )
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
        summary = "Create a new data product version",
        description = "Create a new data product version and associate it to the specified data product"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "[Created](https://www.rfc-editor.org/rfc/rfc9110.html#name-201-created)"
            + "\r\n - Data product version created", 
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = DataProductVersionDPDS.class))
        }),
        @ApiResponse(
            responseCode = "400", 
            description = "[Bad request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
            + "\r\n - Error Code 40001 - Descriptor is empty"
            + "\r\n - Error Code 40007 - Product id is empty",  
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n - Error Code 40401 - Data product not found", 
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
            + "\r\n - Error Code 42202 - Descriptor document syntax is invalid"
            + "\r\n - Error Code 42203 - Descriptor document semantyc is invalid" 
            + "\r\n - Error Code 42205 - Version already exists",  
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),

        @ApiResponse(
            responseCode = "500", 
            description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
            + "\r\n - Error Code 50000 - Error in in the backend service"
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
    public String createDataProductVersion(
        @Parameter(description = "The identifier of the data product to which the new version must be associated")
        @PathVariable String id,

        @Parameter(description = "A data product descriptor document compliant with [DPDS version 1.0.0-DRAFT](https://dpds.opendatamesh.org/resources/specifications/1.0.0-DRAFT/)")
        @Valid @RequestBody String descriptorContent
    )  {

        if(!StringUtils.hasText(id)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                "Id cannot be cannot be empty");
        }

        if(!StringUtils.hasText(descriptorContent)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_01_DESCRIPTOR_IS_EMPTY,
                "Input descriptor document cannot be empty");
        }

        UriLocation descriptorLocation = new UriLocation(descriptorContent);
        String serverUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        DataProductVersion dataProductVersion = dataProductService.addDataProductVersion(id, descriptorLocation, serverUrl);
        DataProductVersionDPDS dataProductVersionDPDS = dataProductVersionMapper.toResource(dataProductVersion);
        
        DataProductVersionSerializer serializer = new DataProductVersionSerializer();
        String serailizedContent = null;
        try {
            serailizedContent = serializer.serialize(dataProductVersionDPDS, "canonical", "json", true);
        } catch (JsonProcessingException e) {
           throw new InternalServerException(
            ODMApiCommonErrors.SC500_02_DESCRIPTOR_ERROR,
            "Impossible to serialize data product version raw content", e);
        }
        return serailizedContent;
    }

    // ----------------------------------------
    // READ All data product versions
    // ----------------------------------------

    @GetMapping(
        value = "/{id}/versions"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get all register definitions",
        description = "Get all definitions registered in the Data Product Registry"
        // ,tags = { "Data Product Versions" }
    ) 
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "All register version numbers of the data product identified by the input `id`", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = String.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "[Bad request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
            + "\r\n - Error Code 40007 - Product id is empty",  
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n - Error Code 40401 - Data product not found",            
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
            + "\r\n - Error Code 50000 - Error in in the backend service"
            + "\r\n - Error Code 50001 - Error in the backend database"
            + "\r\n - Error Code 50002 - Error in in the backend descriptor processor",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public List<String> readAllDataProductVersions(
        @Parameter(description = "The denntifier of the data product")
        @PathVariable String id) 
    {
        if(!StringUtils.hasText(id)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                "Id cannot be cannot be empty");
        }

        if(!dataProductService.dataProductExists(id)) {
            throw new NotFoundException(
                RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND,
                "Data product not found");
        }

        List<String> versions = new ArrayList<>();
        List<DataProductVersion> dataProductVersions = dataProductVersionService.searchDataProductVersions(id);
        versions = dataProductVersions.stream().map(dpv -> dpv.getVersionNumber()).collect(Collectors.toList());
        return versions;
    }

    // ----------------------------------------
    // READ Data product version
    // ----------------------------------------

    @GetMapping(
        value = "/{id}/versions/{version}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get the specified data product version",
        description = "Get the data product version `version` of the data product identified by `id`."
        + "The retuned value is a descriptor document compliant with [DPDS version 1.0.0-DRAFT](https://dpds.opendatamesh.org/resources/specifications/1.0.0-DRAFT/)."
    ) 
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The descriptor document that describe the spcified data product version", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DataProductVersionDPDS.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "[Bad request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
            + "\r\n - Error Code 40004 - Invalid format"
            + "\r\n - Error Code 40007 - Product id is empty"
            + "\r\n - Error Code 40011 - Data product version number is empty",  
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n - Error Code 40401 - Data product not found" 
            + "\r\n - Error Code 40402 - Data product version not found",            
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
            + "\r\n - Error Code 50000 - Error in in the backend service"
            + "\r\n - Error Code 50001 - Error in the backend database"
            + "\r\n - Error Code 50002 - Error in in the backend descriptor processor",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public String readOneDataProductVersion(
        @Parameter(description = "The identifier of the data product")
        @Valid @PathVariable(value = "id") String id,
        
        @Parameter(description = "The data product version number")
        @Valid @PathVariable(value = "version") String version,
        
        @Parameter(
            description = "Format used to serialize the descriptor document. Available formats are:" 
            + "\r\n - normalized (default): TODO" 
            + "\r\n - canonical: TODO"
        )
        @RequestParam(name = "format", required = false) String format
    ) {
        if(!StringUtils.hasText(id)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                "Id cannot be cannot be empty");
        }

        if(!StringUtils.hasText(version)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_11_PRODUCT_VERSION_NUMBER_IS_EMPTY,
                "Data product version number is empty");
        }
        
        
        if(StringUtils.hasText(format) && !(format.equalsIgnoreCase("normalized") || format.equalsIgnoreCase("canonical"))) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_04_INVALID_FORMAT,
                "Format [" + format + "] is not supported");
        }
        
        DataProductVersion dataProductVersion = dataProductVersionService.readDataProductVersion(id, version);

        DataProductVersionDPDS dataProductVersionDPDS = dataProductVersionMapper.toResource(dataProductVersion);
        if(format == null) format = "canonical";
        DataProductVersionSerializer serializer = new DataProductVersionSerializer();
        String serailizedContent = null;
        try {
            serailizedContent = serializer.serialize(dataProductVersionDPDS, format, "json", true);
        } catch (JsonProcessingException e) {
           throw new InternalServerException(
            ODMApiCommonErrors.SC502_70_NOTIFICATION_SERVICE_ERROR,
            "Impossible to serialize data product version raw content", e);
        }
        return serailizedContent;
    }

    // ----------------------------------------
    // DELETE Data product version
    // ----------------------------------------

    @DeleteMapping(
        value = "/{id}/versions/{version}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Delete the specified data product version",
        description = "Delete the data product version `version` of the data product identified by `id`"
    ) 
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Data product version deleted"
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "[Bad request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
            + "\r\n - Error Code 40007 - Product id is empty"
            + "\r\n - Error Code 40011 - Data product version number is empty",  
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n - Error Code 40401 - Data product not found" 
            + "\r\n - Error Code 40402 - Data product version not found",            
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
        ),
        @ApiResponse(
            responseCode = "500", 
            description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
            + "\r\n - Error Code 50000 - Error in in the backend service"
            + "\r\n - Error Code 50001 - Error in the backend database",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public void deleteVersion(
        @PathVariable String id, 
        @PathVariable String version
    ) {
        if(!StringUtils.hasText(id)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_07_PRODUCT_ID_IS_EMPTY,
                "Id cannot be cannot be empty");
        }

        if(!StringUtils.hasText(version)) {
            throw new BadRequestException(
                RegistryApiStandardErrors.SC400_11_PRODUCT_VERSION_NUMBER_IS_EMPTY,
                "Data product version number is empty");
        }

        if(!dataProductService.dataProductExists(id)) {
            throw new NotFoundException(
                RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND,
                "Data product not found");
        }

        dataProductVersionService.deleteDataProductVersion(id, version);
    }
}
