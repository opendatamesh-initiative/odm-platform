package org.opendatamesh.platform.pp.registry.api.controllers;


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
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.core.EntityTypeDPDS;
import org.opendatamesh.platform.core.dpds.model.interfaces.InterfaceComponentsDPDS;
import org.opendatamesh.platform.core.dpds.parser.DPDSSerializer;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping(
    value =  "/products", 
    produces = { "application/vnd.odmp.v1+json", "application/vnd.odmp+json", "application/json"}
)
@Validated
@Tag(
    name = "Data Product Components", 
    description = "Data Product Components")
public abstract class AbstractDataProductVersionComponentsController 
{

    private static final Logger logger = LoggerFactory.getLogger(AbstractDataProductVersionComponentsController.class);

    public AbstractDataProductVersionComponentsController() { 
        logger.debug("Data product version components controller succesfully started");
    }

  
    // ======================================================================================
    // GET /{id}/versions/{version}/ports
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
                schema = @Schema(implementation = InterfaceComponentsDPDS.class))
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
    public String getPortsEndpoint(
        @Parameter(description = "The identifier of the data product")
        @PathVariable(value = "id") String id,
        
        @Parameter(description = "The version number")
        @PathVariable(value = "version") String version,

        @RequestParam(name = "portType", required = false) String portType,

        @RequestParam(name = "format", required = false) String format
       
    )   {
        return getPorts(id, version, portType, format);
    }


    public abstract String getPorts(String id, String version, String portType, String format);
}
