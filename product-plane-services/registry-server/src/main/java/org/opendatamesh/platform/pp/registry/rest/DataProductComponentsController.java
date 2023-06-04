package org.opendatamesh.platform.pp.registry.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

import org.opendatamesh.platform.pp.registry.core.DataProductDescriptor;
import org.opendatamesh.platform.pp.registry.core.DataProductDescriptorValidator;
import org.opendatamesh.platform.pp.registry.core.exceptions.ParseException;
import org.opendatamesh.platform.pp.registry.core.exceptions.UnresolvableReferenceException;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProductVersion;
import org.opendatamesh.platform.pp.registry.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductVersionResource;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.EntityType;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.InterfaceComponentsResource;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DataProductMapper;
import org.opendatamesh.platform.pp.registry.services.DataProductVersionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.ValidationMessage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(
    value =  "/products", 
    produces = { "application/vnd.odmp.v1+json", "application/vnd.odmp+json", "application/json"}
)
@Validated
@Tag(
    name = "Data Product Components", 
    description = "Data Product Components")
public class DataProductComponentsController 
{

    @Autowired
    private DataProductVersionService dataProductVersionService;

    @Autowired
    private DataProductMapper dataProductMapper;

    @Autowired
    DataProductDescriptorValidator dataProductDescriptorValidator;

    @Autowired
    ObjectMapper objectMapper;

    
    private static final Logger logger = LoggerFactory.getLogger(DataProductComponentsController.class);

    public DataProductComponentsController() { 
        logger.debug("Data product components controller succesfully started");
    }

  
    // ======================================================================================
    // COMPONENTS
    // ======================================================================================
    
    @RequestMapping(
        value = "/{id}/versions/{version}/ports", 
        method = RequestMethod.GET
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get all the ports of the specified data product version",
        description = "Get all the ports of the specified data product version grouped by port type."
        //, tags = { "Data Product Components" }
    ) 
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "TODO: the api definition", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = InterfaceComponentsResource.class))
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "[Bad request](https://www.rfc-editor.org/rfc/rfc9110.html#name-400-bad-request)"
            + "\r\n - Invalid port type" 
            + "\r\n - Invalid format", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
            + "\r\n -  Data product does not exists"
            + "\r\n -  Data product's version does not exists", 
            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))
        )
    })
    public String getPorts(
        @Parameter(description = "The identifier of the data product")
        @Valid @PathVariable(value = "id") String id,
        
        @Parameter(description = "The version number")
        @Valid @PathVariable(value = "version") String version,

        @RequestParam(name = "portType", required = false) String portType,

        @RequestParam(name = "format", required = false) String format
       
    ) throws JsonProcessingException  {
        
        EntityType entityType;
        if(portType == null) {
            entityType = null;
        } else {
            entityType = EntityType.get(portType);
            if(entityType == null || !entityType.isPort()){
                throw new BadRequestException(
                    OpenDataMeshAPIStandardError.SC400_06_INVALID_PORTTYPE,
                    "Value [" + portType + "] is not a valid port type"
                );
            }
        }
        
        if(format == null) {
            format = "normalized";
        } else {
            if(!format.equals("normalized") || !format.equals("canonical")) {
                throw new BadRequestException(
                    OpenDataMeshAPIStandardError.SC400_04_INVALID_FORMAT,
                    "Value [" + format + "] is not a valid format"
                );
            }
        }

        DataProductVersion dataProductVersion = dataProductVersionService.readDataProductVersion(id, version);
        DataProductVersionResource dataProductVersionResource = dataProductMapper.toResource(dataProductVersion);
       

        switch (format) {
           case "normalized": //parsed=deserialized and then serialized again
            HashMap<String, List> interfaceComponentsRawProperties = null;
            if(entityType != null) {
                interfaceComponentsRawProperties = dataProductVersionResource.getInterfaceComponents().getRawContent(entityType);
            } else {
                interfaceComponentsRawProperties = dataProductVersionResource.getInterfaceComponents().getRawContent();
            }
            return objectMapper.writeValueAsString(interfaceComponentsRawProperties);
           case "canonical": //normalized + semantic equalization
                return objectMapper.writeValueAsString(dataProductVersionResource.getInterfaceComponents());
        }
        throw new BadRequestException(
            OpenDataMeshAPIStandardError.SC400_04_INVALID_FORMAT,
            "Format [" + format + "] is not supported");
    }
}
