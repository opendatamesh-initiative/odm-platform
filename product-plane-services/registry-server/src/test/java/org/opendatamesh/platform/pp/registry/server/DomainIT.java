package org.opendatamesh.platform.pp.registry.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.registry.api.v1.exceptions.ODMRegistryAPIStandardError;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DomainResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.ErrorRes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;


@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DomainIT extends ODMRegistryIT {

    // ======================================================================================
    // HAPPY PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Domain
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDomainCreate()
    throws IOException {

        DomainResource domainRequest;

        // TEST 1: create first domain
        domainRequest = createDomain1();
        assertThat(domainRequest.getId())
                .isEqualTo(UUID.nameUUIDFromBytes("urn:odmp:org.opendatamesh:domains:Disney".getBytes()).toString());
        assertThat(domainRequest.getFullyQualifiedName()).isEqualTo("urn:odmp:org.opendatamesh:domains:Disney");
        assertThat(domainRequest.getName()).isEqualTo("Disney");
        assertThat(domainRequest.getDescription()).isEqualTo("Domain for all Disney entities");

    }

    // ----------------------------------------
    // READ Data product
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDomainReadAll() throws IOException {

        DomainResource domainRequest;

        // create first domain
        domainRequest = createDomain1();


        // TEST 1: verify first Domain

        ResponseEntity<DomainResource[]> getDomainResponse = null;
        getDomainResponse = registryClient.readAllDomains();
        verifyResponseEntity(getDomainResponse, HttpStatus.OK, true);
        assertThat(getDomainResponse.getBody().length).isEqualTo(1);

        DomainResource domain2 = resourceBuilder.buildDomain("urn:odmp:org.opendatamesh:domains:Domain2");
        registryClient.createDomain(domain2);


        // TEST 1: verify second Domain

        getDomainResponse = registryClient.readAllDomains();
        verifyResponseEntity(getDomainResponse, HttpStatus.OK, true);
        assertThat(getDomainResponse.getBody().length).isEqualTo(2);


    }
/*
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
    public void testDataProductDelete() throws IOException {

        DataProductResource dataProductResource = createDataProduct(RESOURCE_DP1);

        ResponseEntity deleteResponse = registryClient.deleteDataProduct(dataProductResource.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

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
                HttpStatus.BAD_REQUEST, ODMRegistryAPIStandardError.SC400_10_PRODUCT_IS_EMPTY);
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
                ODMRegistryAPIStandardError.SC422_04_PRODUCT_ALREADY_EXISTS);


        // TEST 2: try to register a product without setting the fqn
        dataProductRes = resourceBuilder.buildDataProduct(null, "marketing", "marketing product");
        errorResponse = registryClient.postDataProduct(dataProductRes, ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                ODMRegistryAPIStandardError.SC422_07_PRODUCT_IS_INVALID);
        
        // TEST 3: try to register a product with an empty fqn
        dataProductRes = resourceBuilder.buildDataProduct("    ", "marketing", "marketing product");
        errorResponse = registryClient.postDataProduct(dataProductRes, ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                ODMRegistryAPIStandardError.SC422_07_PRODUCT_IS_INVALID);
        
    
        // TEST 4: try to register a product setting an id that not match with the fqn
        dataProductRes = resourceBuilder.buildDataProduct("wrong-id", "prod-5", "marketing", "marketing product");
        errorResponse = registryClient.postDataProduct(dataProductRes, ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                ODMRegistryAPIStandardError.SC422_07_PRODUCT_IS_INVALID);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductUpdate4xxErrors() throws IOException {

        // TEST 1: NULL payload
        ResponseEntity<ErrorRes> errorResponse = registryClient.putDataProduct(null, ErrorRes.class);
        verifyResponseError(
                errorResponse,
                HttpStatus.BAD_REQUEST,
                ODMRegistryAPIStandardError.SC400_10_PRODUCT_IS_EMPTY
        );

        // TEST 2: Empty fqn
        DataProductResource dataProductResource = new DataProductResource();
        dataProductResource.setDescription("test");
        dataProductResource.setDomain("test");
        errorResponse = registryClient.putDataProduct(dataProductResource, ErrorRes.class);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                ODMRegistryAPIStandardError.SC422_07_PRODUCT_IS_INVALID
        );

        // TEST 3: Not found
        dataProductResource.setFullyQualifiedName("test");
        errorResponse = registryClient.putDataProduct(dataProductResource, ErrorRes.class);
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                ODMRegistryAPIStandardError.SC404_01_PRODUCT_NOT_FOUND
        );

    }


    // ----------------------------------------
    // OTHER ...
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductMediaTypeErrors() {
        // TODO test the acceptable media types for create and update endpoints

        // TODO test one wrong media type for create and update endpoints
    }*/
}