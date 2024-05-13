package org.opendatamesh.platform.pp.registry.api.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(
    value =  "/products", 
    produces = { 
        "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", 
        "application/json"
    }
)
@Validated
@Tag(
    name = "Data Products", 
    description = "Endpoints associated to products collection")
public abstract class AbstractDataProductController 
{
    private static final Logger logger = LoggerFactory.getLogger(AbstractDataProductController.class);

    public AbstractDataProductController() { 
        logger.debug("Data product controller successfully started");
    }

    // @see https://github.com/swagger-api/swagger-core/wiki/Swagger-2.X---Annotations#arrayschema

    // ===============================================================================
    // POST /products  
    // ===============================================================================
   
    @PostMapping(
        consumes = { "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @Operation(
        summary = "Register the the data product",
        description = "Register the provided data product in the Data Product Registry" 
     
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "201", 
            description = "Data product registered", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DataProductResource.class)
            )
        ),
        @ApiResponse(
            responseCode = "422", 
            description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
            + "\r\n - Error Code 42207 - Data product is invalid"
            + "\r\n - Error Code 42204 - Data product already exists",  
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        ),
         @ApiResponse(
            responseCode = "500", 
            description = "[Internal Server Error](https://www.rfc-editor.org/rfc/rfc9110.html#name-500-internal-server-error)"
            + "\r\n - Error Code 50001 - Error in the backend database"
            + "\r\n - Error Code 50002 - Error in in the backend service", 
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
    public DataProductResource createDataProductEndpoint(
        @Parameter(description = "A data product object", required = true)
        @RequestBody DataProductResource dataProductRes
    ) throws Exception {
        return createDataProduct(dataProductRes);
    }
    
    public abstract  DataProductResource createDataProduct(DataProductResource dataProductRes);


    // ===============================================================================
    // GET /products
    // ===============================================================================

    // TODO add all error responses
    
    // TODO find out how to specify in the main response (code 200) that the schema is a 
    // list of DataProductResource
    
    // TODO at the moment ownerId is not part of DataProduct. Add it in the resource, entity 
    // and database schema then test the serch also on this property

    
    @GetMapping
    @ResponseStatus(HttpStatus.OK) 
    @Operation(
        summary = "Get all registered data products",
        description = "Get all data products registered in the Data Product Registry."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "All registered data products", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DataProductResource.class)
            )
        )
    })
    public List<DataProductResource> getDataProductsEndpoint(
        @Parameter(description="Add `fqn` parameter to the request to get only data products with the specified fullyQualifiedName")
        @RequestParam(required = false, name = "fqn") String fqn, 

        @Parameter(description="Add `domain` parameter to the request to get only data products belonging to a specific domain")
        @RequestParam(required = false, name = "domain") String domain)
    {
        return getDataProducts(fqn, domain);
    }

    public abstract List<DataProductResource> getDataProducts(String fqn, String domain);

    // ===============================================================================
    // GET /products/{id}
    // ===============================================================================

    // TODO add all error responses

    @GetMapping(
        value = "/{id}"
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Get the specified data product",
        description = "Get the data product identified by the input `id`"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "The requested data product", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = DataProductResource.class)
            )
        )
    })
    public DataProductResource getDataProductEndpoint(
        @Parameter(description = "Identifier of the data product")
        @PathVariable(value = "id") String id) 
    {
        return getDataProduct(id);
    }

    public abstract DataProductResource getDataProduct(String id) ;

    // ===============================================================================
    // DELETE /products/{id}
    // ===============================================================================
    
    // TODO add all error responses

    

    @RequestMapping(
        value = "/{id}", 
        method = RequestMethod.DELETE
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Delete the specified data product",
        description = "Delete the data product identified by the input `id` and all its associated versions"
    )
    public DataProductResource deleteDataProductEndpoint(
        @Parameter(description = "Identifier of the data product")
        @PathVariable String id
    ) {
        return deleteDataProduct(id);
    }

    public abstract DataProductResource deleteDataProduct(String id);

    // ===============================================================================
    // PUT /products
    // ===============================================================================
    
    // TODO all properties, except fqn and id, should be editable. Modify to 
    // pass the new version into request payload. Remove url parameters. Do all
    // the required test on input and throw exception in needed 

    @PutMapping(
        consumes = { "application/vnd.odmp.v1+json", "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.OK)
    @Operation(
        summary = "Update a data product",
        description = "Update the provided data product"
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Data product updated",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = DataProductResource.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "[Not Found](https://www.rfc-editor.org/rfc/rfc9110.html#name-404-not-found)"
                            + "\r\n - Error Code 40401 - Data Product not found",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
            ),
            @ApiResponse(
                    responseCode = "422",
                    description = "[Unprocessable Content](https://www.rfc-editor.org/rfc/rfc9110.html#name-422-unprocessable-content)"
                            + "\r\n - Error Code 42207 - Data product is invalid",
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
    public DataProductResource updateProductEndpoint(
        @Parameter(
                description = "A data product object",
                required = true)
        @RequestBody DataProductResource dataProductRes
    ) throws Exception {
        return updateProduct(dataProductRes);
    }

    public abstract DataProductResource updateProduct(DataProductResource dataProductRes);
}
