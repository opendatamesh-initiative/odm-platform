package org.opendatamesh.platform.pp.registry.server.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.pp.registry.api.v1.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.api.v1.exceptions.ODMRegistryAPIStandardError;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DomainResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.server.database.entities.dataproduct.Domain;
import org.opendatamesh.platform.pp.registry.server.database.mappers.DomainMapper;
import org.opendatamesh.platform.pp.registry.server.services.DomainService;
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
    value =  "/domains",
    produces = { 
        "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", 
        "application/json"
    }
)
@Validated
@Tag(
    name = "Domains",
    description = "Domains")
public class DomainController
{
    @Autowired
    private DomainService domainService;

    @Autowired
    private DomainMapper domainMapper;

    @Autowired
    ObjectMapper objectMapper;

    private static final Logger logger = LoggerFactory.getLogger(DomainController.class);

    public DomainController() {
        logger.debug("Domain controller successfully started");
    }

  
    // ======================================================================================
    // DOMAINS
    // ======================================================================================
    
    // ----------------------------------------
    // CREATE Domain
    // ----------------------------------------
   
    @PostMapping(
        consumes = { "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @Operation(
        summary = "Register the domain",
        description = "Register the provided domain in the Domain Registry"

    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Domain registered",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DomainResource.class)
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
            + "\r\n - Error Code 42215 - Domain is invalid"
            + "\r\n - Error Code 42216 - Domain product already exists",
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
    public DomainResource createDomain(
        @Parameter( 
            description = "A domain object",
            required = true)
        @Valid @RequestBody(required=false)  DomainResource domainRes
    ) throws Exception {
        if(domainRes == null) {
            throw new BadRequestException(
                ODMRegistryAPIStandardError.SC400_16_DOMAIN_IS_EMPTY,
                "Domain cannot be empty");
        }
        domainRes.initDomainFQNAndID();
        Domain domain = domainMapper.toEntity(domainRes);
        domain = domainService.createDomain(domain);
        return domainMapper.toResource(domain);
    }

    // ----------------------------------------
    // READ All Products 
    // ----------------------------------------

    // TODO add all error responses
    
    // TODO find out how to specify in the main response (code 200) that the schema is a 
    // list of DataProductResource
    
    // TODO at the moment ownerId is not part of DataProduct. Add it in the resource, entity 
    // and database schema then test the serch also on this property

    @GetMapping
    @ResponseStatus(HttpStatus.OK) 
    @Operation(
        summary = "Get all registered domains",
        description = "Get all domains registered in the Domain Registry."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "All registered domains",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DomainResource.class)
            )
        )
    })
    public List<DomainResource> getAllDomains()
    {
        return domainMapper.domainToResources(domainService.readAllDomains());
    }

    // ----------------------------------------
    // READ Product
    // ----------------------------------------

    // TODO add all error responses

    @GetMapping(
        value = "/{id}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get the specified domain",
        description = "Get the domain identified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested domain",
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DomainResource.class)
            )
        )
    })
    public DomainResource getDomain(
        @Parameter(description = "Identifier of the domain")
        @Valid @PathVariable(value = "id") String id) 
    {
        Domain domain = domainService.searchDomain(id);
        DomainResource domainResource = domainMapper.toResource(domain);
        return domainResource;
    }

    // ----------------------------------------
    // DELETE Product
    // ----------------------------------------

    // TODO add all error responses. What happens to all the data products with this domain?

    @RequestMapping(
        value = "/{id}", 
        method = RequestMethod.DELETE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Delete the specified domain",
        description = "Delete the domain identified by the input `id`"
        
    )
    public void deleteDomain(
        @Parameter(description = "Identifier of the domain")
        @PathVariable String id
    )
    {
        domainService.deleteDomain(id);
    }

    // ----------------------------------------
    // UPDATE Product
    // ----------------------------------------
    
    // TODO all properties, except fqn and id, should be editable. Modify to 
    // pass the new version into request payload. Remove url parameters. Do all
    // the required test on input and throw exception in needed 

    @PutMapping(
        consumes = { "application/vnd.odmp.v1+json", "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Update a domain",
        description = "Update the provided domain"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Domain updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataProductResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40406 - Domain not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42215 - Domain is invalid",
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
    public DomainResource updateProduct(
        @Parameter(
                description = "A domain object",
                required = true)
        @Valid @RequestBody(required=false)  DomainResource domainRes
    ) throws Exception {

        if(domainRes == null)
            throw new BadRequestException(
                ODMRegistryAPIStandardError.SC400_16_DOMAIN_IS_EMPTY,
                "Domain is empty"
            );

        Domain domain = domainMapper.toEntity(domainRes);
        domain = domainService.updateDomain(domain);

        return domainMapper.toResource(domain);
    }
}
