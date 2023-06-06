package org.opendatamesh.platform.pp.registry.rest;

import java.util.List;

import javax.validation.Valid;

import org.opendatamesh.platform.pp.registry.core.DataProductDescriptorValidator;
import org.opendatamesh.platform.pp.registry.database.entities.dataproduct.DataProduct;
import org.opendatamesh.platform.pp.registry.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductResource;
import org.opendatamesh.platform.pp.registry.resources.v1.mappers.DataProductMapper;
import org.opendatamesh.platform.pp.registry.services.DataProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

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
    produces = { 
        "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", 
        "application/json"
    }
)
@Validated
@Tag(
    name = "Data Products", 
    description = "Data Products")
public class DataProductController 
{
    @Autowired
    private DataProductService dataProductService;

    @Autowired
    private DataProductMapper dataProductMapper;

    @Autowired
    DataProductDescriptorValidator dataProductDescriptorValidator;

    @Autowired
    ObjectMapper objectMapper;

    
    private static final Logger logger = LoggerFactory.getLogger(DataProductController.class);

    public DataProductController() { 
        logger.debug("Data product controller succesfully started");
    }

  
    // ======================================================================================
    // PRODUCTS
    // ======================================================================================
    
    // ----------------------------------------
    // CREATE Product 
    // ----------------------------------------
   
    @PostMapping(
        consumes = { "application/vnd.odmp.v1+json", 
        "application/vnd.odmp+json", "application/json"}
    )
    @ResponseStatus(HttpStatus.CREATED) 
    @Operation(
        summary = "Register the the data product",
        description = "Register the provided data product in the Data Product Registry" 
        //, tags = { "Data Products" }
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
            + "\r\n - Error Code 50204 - Invalid metaService's response",
            content = {@Content(mediaType = "application/json", schema = @Schema(implementation = ErrorRes.class))}
        )
    })
    public DataProductResource createDataProduct(
        @Parameter( 
            description = "A data product object", 
            required = true)
        @Valid @RequestBody(required=false)  DataProductResource dataProductRes
    ) throws Exception {
        if(dataProductRes == null) {
            throw new BadRequestException(
                OpenDataMeshAPIStandardError.SC400_10_PRODUCT_IS_EMPTY,
                "Data product cannot be empty");
        }
        DataProduct dataProduct = dataProductMapper.toEntity(dataProductRes);
        dataProduct = dataProductService.createDataProduct(dataProduct);
        return dataProductMapper.toResource(dataProduct);
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
    public List<DataProductResource> getAllDataProducts(
        @Parameter(description="Add `domain` parameter to the request to get only data products belonging to a specific domain")
        @RequestParam(required = false, name = "domain") String domain,
        
        @Parameter(description="Add `ownerId` parameter to the request to get only data products owned by a specific person")
        @RequestParam(required = false, name = "ownerId") String ownerId) 
    {
        return dataProductMapper.dataProductsToResources(dataProductService.searchDataProductsByDomainAndOwner(domain, ownerId));
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
    public DataProductResource getDataProduct(
        @Parameter(description = "Idenntifier of the data product")
        @Valid @PathVariable(value = "id") String id) 
    {
        DataProduct dataProduct = dataProductService.readDataProduct(id);
        DataProductResource dataProductResource = dataProductMapper.toResource(dataProduct);
        return dataProductResource;
    }

    // ----------------------------------------
    // DELETE Product
    // ----------------------------------------

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
    public void deleteDataProduct(
        @Parameter(description = "Identifier of the data product")
        @PathVariable String id
    )
    {
        dataProductService.deleteDataProduct(id);
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
    public DataProductResource updateProduct(
        @Parameter(
                description = "A data product object",
                required = true)
        @Valid @RequestBody(required=false)  DataProductResource dataProductRes
    ) throws Exception {

        if(dataProductRes == null)
            throw new BadRequestException(
                OpenDataMeshAPIStandardError.SC400_10_PRODUCT_IS_EMPTY,
                "Domain is empty"
            );

        DataProduct dataProduct = dataProductMapper.toEntity(dataProductRes);
        dataProduct = dataProductService.updateDataProduct(dataProduct);

        return dataProductMapper.toResource(dataProduct);
    }
}
