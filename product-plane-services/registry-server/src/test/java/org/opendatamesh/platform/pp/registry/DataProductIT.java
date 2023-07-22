package org.opendatamesh.platform.pp.registry;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

//TODO every update to data product must check and mock the call to the policyservice

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DataProductIT extends OpenDataMeshIT {

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

        DataProductResource dataProductRequest, dataProductResponse;

        // TEST 1: create first data product
        dataProductRequest = createDataProduct(RESOURCE_DP1);
        assertThat(dataProductRequest.getId())
                .isEqualTo(UUID.nameUUIDFromBytes("urn:org.opendatamesh:dataproduct:tripExecution".getBytes()).toString());
        assertThat(dataProductRequest.getFullyQualifiedName()).isEqualTo("urn:org.opendatamesh:dataproduct:tripExecution");
        assertThat(dataProductRequest.getDescription()).isEqualTo("This is prod-1");
        assertThat(dataProductRequest.getDomain()).isEqualTo("Disney");

        // TEST 2: create another data product setting only the fqn property
        dataProductRequest = resourceBuilder.buildDataProduct("prod-2", null, null);
        postProductResponse = registryClient.postDataProduct(dataProductRequest);
        verifyResponseEntity(postProductResponse, HttpStatus.CREATED, true);
        dataProductResponse = postProductResponse.getBody();
        if(dataProductResponse != null) {
            assertThat(dataProductResponse.getId()).isEqualTo(UUID.nameUUIDFromBytes("prod-2".getBytes()).toString());
            assertThat(dataProductResponse.getFullyQualifiedName()).isEqualTo("prod-2");
            assertThat(dataProductResponse.getDescription()).isNull();
            assertThat(dataProductResponse.getDomain()).isNull();
        } else {
            fail("Response body is null");
        }
        
        // TEST 3: create another data product setting all properties including the id
        dataProductRequest = resourceBuilder.buildDataProduct(UUID.nameUUIDFromBytes("prod-3".getBytes()).toString(), "prod-3", "Disney", "This is prod-3");
        postProductResponse = registryClient.postDataProduct(dataProductRequest);
        verifyResponseEntity(postProductResponse, HttpStatus.CREATED, true);
        dataProductResponse = postProductResponse.getBody();
        if(dataProductResponse != null) {
            assertThat(dataProductResponse.getId()).isEqualTo(UUID.nameUUIDFromBytes("prod-3".getBytes()).toString());
            assertThat(dataProductResponse.getFullyQualifiedName()).isEqualTo("prod-3");
            assertThat(dataProductResponse.getDescription()).isEqualTo("This is prod-3");
            assertThat(dataProductResponse.getDomain()).isEqualTo("Disney");
        } else {
            fail("Response body is null");
        }
    }

    // ----------------------------------------
    // READ Data product
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductReadAll() throws IOException {

        ResponseEntity<DataProductResource> postProductResponse = null;
        DataProductResource dataProduct1Request, dataProduct2Request, dataProduct3Request;
    
        dataProduct1Request = resourceBuilder.buildDataProduct("prod-1", "marketing", "marketing product");
        postProductResponse = registryClient.postDataProduct(dataProduct1Request);
        verifyResponseEntity(postProductResponse, HttpStatus.CREATED, true);
        postProductResponse.getBody();

        dataProduct2Request = resourceBuilder.buildDataProduct("prod-2", "sales", "sales product");
        postProductResponse = registryClient.postDataProduct(dataProduct2Request);
        verifyResponseEntity(postProductResponse, HttpStatus.CREATED, true);
        postProductResponse.getBody();
        
        dataProduct3Request = resourceBuilder.buildDataProduct("prod-3", "hr", "hr product");
        postProductResponse = registryClient.postDataProduct(dataProduct3Request);
        verifyResponseEntity(postProductResponse, HttpStatus.CREATED, true);
        postProductResponse.getBody();

        ResponseEntity<DataProductResource[]> getProducteResponse = registryClient.getDataProducts();
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

        ResponseEntity<DataProductResource> postProductResponse = null;
        DataProductResource dataProductRequest;

        dataProductRequest = resourceBuilder.buildDataProduct("prod-1", "marketing", "marketing product");
        postProductResponse = registryClient.postDataProduct(dataProductRequest);
        verifyResponseEntity(postProductResponse, HttpStatus.CREATED, true);

        dataProductRequest = resourceBuilder.buildDataProduct("prod-2", "sales", "sales product");
        postProductResponse = registryClient.postDataProduct(dataProductRequest);
        verifyResponseEntity(postProductResponse, HttpStatus.CREATED, true);

        dataProductRequest = resourceBuilder.buildDataProduct("prod-3", "hr", "hr product");
        postProductResponse = registryClient.postDataProduct(dataProductRequest);
        verifyResponseEntity(postProductResponse, HttpStatus.CREATED, true);

        ResponseEntity<DataProductResource> getDataProductResponse = registryClient.getDataProductByFqn("prod-1");
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

        createDataProduct(RESOURCE_DP1);
        DataProductResource dataProductRes = updateDataProduct(RESOURCE_DP1_UPD);

        // TEST 1: create first data product
        assertThat(dataProductRes.getId())
                .isEqualTo(UUID.nameUUIDFromBytes("urn:org.opendatamesh:dataproduct:tripExecution".getBytes()).toString());
        assertThat(dataProductRes.getFullyQualifiedName()).isEqualTo("urn:org.opendatamesh:dataproduct:tripExecution");
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

        ResponseEntity<ErrorRes> errorResponse = null;

        // Test error SC400_01_DESCRIPTOR_IS_EMPTY
        String payload = null;
        errorResponse = registryClient.postDataProduct(payload, ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.BAD_REQUEST, OpenDataMeshAPIStandardError.SC400_10_PRODUCT_IS_EMPTY);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductCreateError422Errors() throws IOException {

        ResponseEntity<ErrorRes> errorResponse = null;

        DataProductResource dataProductRes = createDataProduct(RESOURCE_DP1);

        // TEST 1: try to register the same product again
        errorResponse = registryClient.postDataProduct(dataProductRes, ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_04_PRODUCT_ALREADY_EXISTS);


        // TEST 2: try to register a product without setting the fqn
        dataProductRes = resourceBuilder.buildDataProduct(null, "marketing", "marketing product");
        errorResponse = registryClient.postDataProduct(dataProductRes, ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_07_PRODUCT_IS_INVALID);
        
        // TEST 3: try to register a product with an empty fqn
        dataProductRes = resourceBuilder.buildDataProduct("    ", "marketing", "marketing product");
        errorResponse = registryClient.postDataProduct(dataProductRes, ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_07_PRODUCT_IS_INVALID);
        
    
        // TEST 4: try to register a product setting an id that not match with the fqn
        dataProductRes = resourceBuilder.buildDataProduct("wrong-id", "prod-5", "marketing", "marketing product");
        errorResponse = registryClient.postDataProduct(dataProductRes, ErrorRes.class);
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
}