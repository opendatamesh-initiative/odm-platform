package org.opendatamesh.platform.pp.registry.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.resources.DomainResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public abstract class AbstractDomainController
{

    private static final Logger logger = LoggerFactory.getLogger(AbstractDomainController.class);

    public AbstractDomainController() {
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
                            + "\r\n - Error Code 42216 - Domain already exists",
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
    public DomainResource createDomainEndpoint(
            @Parameter(
                    description = "A domain object",
                    required = true)
            @Valid @RequestBody(required=false)  DomainResource domainRes
    ) throws Exception {
        return createDomain(domainRes);
    }

    public abstract DomainResource createDomain(DomainResource domainRes) throws Exception;


    // ----------------------------------------
    // READ All Domains
    // ----------------------------------------


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
    public List<DomainResource> getAllDomainsEndpoint()
    {
        return getAllDomains();
    }
    public abstract List<DomainResource> getAllDomains();

    // ----------------------------------------
    // READ Domain
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
    public DomainResource getDomainEndpoint(
            @Parameter(description = "Identifier of the domain")
            @Valid @PathVariable(value = "id") String id)
    {
        return getDomain(id);
    }

    public abstract DomainResource getDomain(String id);

    // ----------------------------------------
    // DELETE Domain
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
    public void deleteDomainEndpoint(
            @Parameter(description = "Identifier of the domain")
            @PathVariable String id
    ) {
        deleteDomain(id);
    }

    public abstract void deleteDomain(String id) ;

    // ----------------------------------------
    // UPDATE Domain
    // ----------------------------------------

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
                            schema = @Schema(implementation = DomainResource.class)
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
    public DomainResource updateDomainEndpoint(
            @Parameter(
                    description = "A domain object",
                    required = true)
            @Valid @RequestBody(required=false)  DomainResource domainRes
    ) throws Exception {
        return updateDomain(domainRes);
    }

    public abstract DomainResource updateDomain(DomainResource domainRes) throws Exception;
}
