package org.opendatamesh.dpexperience.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.opendatamesh.platform.pp.registry.core.DataProductDescriptor;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.opendatamesh.platform.pp.registry.resources.v1.dataproduct.DataProductResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import com.fasterxml.jackson.databind.ObjectMapper;

//TODO every update to data product must check and mock the call to the policyservice

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DataProductIT extends OpenDataMeshIT {

    ObjectMapper objectMapper;

    @Before
    public void setup() {
        objectMapper = DataProductDescriptor.buildObjectMapper();
    }

    // ======================================================================================
    // HAPPY PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Data product
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductCreate() 
    throws IOException {

        ResponseEntity<DataProductResource> postProductResponse = null;

        DataProductResource dataProductRes = createDataProduct1();

        // TEST 1: create first data product
        assertThat(dataProductRes.getId())
                .isEqualTo(UUID.nameUUIDFromBytes("urn:dpds:it.quantyca:dataproducts:sampleDomain:tripExecution".getBytes()).toString());
        assertThat(dataProductRes.getFullyQualifiedName()).isEqualTo("urn:dpds:it.quantyca:dataproducts:sampleDomain:tripExecution");
        assertThat(dataProductRes.getDescription()).isEqualTo("This is prod-1");
        assertThat(dataProductRes.getDomain()).isEqualTo("Disney");

        // TEST 2: create another data product setting only the fqn property
        postProductResponse = rest.createDataProduct("prod-2", null, null);
        verifyResponseEntity(postProductResponse, HttpStatus.CREATED, true);
       
        assertThat(postProductResponse.getBody().getId())
                .isEqualTo(UUID.nameUUIDFromBytes("prod-2".getBytes()).toString());
        assertThat(postProductResponse.getBody().getFullyQualifiedName()).isEqualTo("prod-2");
        assertThat(postProductResponse.getBody().getDescription()).isNull();
        assertThat(postProductResponse.getBody().getDomain()).isNull();

        // TEST 3: create another data product setting all properties including the id
        postProductResponse = rest.createDataProduct(UUID.nameUUIDFromBytes("prod-3".getBytes()).toString(), "prod-3", "Disney", "This is prod-3");
        verifyResponseEntity(postProductResponse, HttpStatus.CREATED, true);
        
        assertThat(postProductResponse.getBody().getId())
                .isEqualTo(UUID.nameUUIDFromBytes("prod-3".getBytes()).toString());
        assertThat(postProductResponse.getBody().getFullyQualifiedName()).isEqualTo("prod-3");
        assertThat(postProductResponse.getBody().getDescription()).isEqualTo("This is prod-3");
        assertThat(postProductResponse.getBody().getDomain()).isEqualTo("Disney");
    }

    // ----------------------------------------
    // READ Data product
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductReadAll() throws IOException {

        rest.createDataProduct("prod-1", "marketing", "marketing product");
        rest.createDataProduct("prod-2", "sales", "sales product");
        rest.createDataProduct("prod-3", "hr", "hr product");

        ResponseEntity<DataProductResource[]> getProducteResponse = rest.readAllDataProducts();
        verifyResponseEntity(getProducteResponse, HttpStatus.OK, true);
        
        assertThat(getProducteResponse.getBody().length).isEqualTo(3);

        // TODO test also content of each data product in the response body
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductSearch() {
        // TODO test search endpoints (i.e. read with filters)
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductReadOne() throws IOException {

        DataProductResource dataProdRes = null;

        ResponseEntity<DataProductResource> response1 
            =  rest.createDataProduct("prod-1", "marketing", "marketing product");
        ResponseEntity<DataProductResource> response2 
            =  rest.createDataProduct("prod-2", "sales", "sales product");
        ResponseEntity<DataProductResource> response3 
            =  rest.createDataProduct("prod-3", "hr", "hr product");

    
        ResponseEntity<DataProductResource> getDataProductResponse = rest.readOneDataProduct("prod-1");
        verifyResponseEntity(getDataProductResponse, HttpStatus.OK, true);
        assertThat(getDataProductResponse.getBody().getDomain()).isEqualTo("marketing");

        // TODO test also other property values
    }

    // ----------------------------------------
    // UPDATE Data product
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductUpdate() throws IOException {

        ResponseEntity<DataProductResource> postProductResponse = null;

        createDataProduct1();
        DataProductResource dataProductRes = updateDataProduct1();

        // TEST 1: create first data product
        assertThat(dataProductRes.getId())
                .isEqualTo(UUID.nameUUIDFromBytes("urn:dpds:it.quantyca:dataproducts:sampleDomain:tripExecution".getBytes()).toString());
        assertThat(dataProductRes.getFullyQualifiedName()).isEqualTo("urn:dpds:it.quantyca:dataproducts:sampleDomain:tripExecution");
        assertThat(dataProductRes.getDescription()).isEqualTo("This is prod-1 - updated");
        assertThat(dataProductRes.getDomain()).isEqualTo("Disney - updated");

    }

    // ----------------------------------------
    // DELETE Data product
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductDelete() {
        // TODO
    }

   

    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Data product
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductCreateError400Errors() throws IOException {

        HttpEntity<DataProductResource> entity = null;
        ResponseEntity<ErrorRes> errorResponse = null;

        // Test error SC400_01_DESCRIPTOR_IS_EMPTY
        entity = rest.getProductDocumentAsHttpEntity(null);
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS),
                entity,
                ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.BAD_REQUEST, OpenDataMeshAPIStandardError.SC400_10_PRODUCT_IS_EMPTY);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductCreateError422Errors() throws IOException {

        HttpEntity<DataProductResource> entity = null;
        ResponseEntity<ErrorRes> errorResponse = null;

        DataProductResource dataProductRes = createDataProduct1();

        // TEST 1: try to register the same product again
        entity = rest.getProductDocumentAsHttpEntity(RESOURCE_DP1);
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS),
                entity,
                ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_04_PRODUCT_ALREADY_EXISTS);


        // TEST 2: try to register a product without setting the fqn
        entity.getBody().setFullyQualifiedName(null);
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS),
                entity,
                ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_07_PRODUCT_IS_INVALID);

        // TEST 3: try to register a product setting an id that not match with the fqn
        entity.getBody().setId("wrong-id");
        entity.getBody().setFullyQualifiedName("prod-5");
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS),
                entity,
                ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_07_PRODUCT_IS_INVALID);
    }

    // ----------------------------------------
    // UPDATE Data product
    // ----------------------------------------

    // TODO

    // ----------------------------------------
    // DELETE Data product
    // ----------------------------------------

    // TODO


    // ----------------------------------------
    // OTHER ...
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductMediaTypeErrors() {
        // TODO test the acceptable media types for create and update endpoints

        // TODO test one wrong media type for create and update endpoints
    }

   

    // ======================================================================================
    // PRIVATE METHODS
    // ======================================================================================

    // ----------------------------------------
    // Create test resources
    // ----------------------------------------

    // TODO: ...as needed

    // ----------------------------------------
    // Verify test resources
    // ----------------------------------------

    // TODO: ...as needed
}