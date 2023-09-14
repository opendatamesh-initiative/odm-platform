package org.opendatamesh.platform.pp.registry.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.api.resources.DomainResource;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryTestResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;


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
    // READ Domain
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDomainReadAll() throws IOException {

        // create first domain
        DomainResource domainRequest = createDomain1();

        // TEST 1: verify first Domain

        ResponseEntity<DomainResource[]> getDomainResponse = null;
        getDomainResponse = registryClient.readAllDomains();
        verifyResponseEntity(getDomainResponse, HttpStatus.OK, true);
        assertThat(getDomainResponse.getBody().length).isEqualTo(1);

        DomainResource domain2 = resourceBuilder.buildDomain("urn:odmp:org.opendatamesh:domains:Domain2");
        registryClient.createDomain(domain2);


        // TEST 2: verify second Domain

        getDomainResponse = registryClient.readAllDomains();
        verifyResponseEntity(getDomainResponse, HttpStatus.OK, true);
        assertThat(getDomainResponse.getBody().length).isEqualTo(2);


    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDomainGetOneById() throws IOException {

        DomainResource domainRequest = createDomain1();

        // TEST 1: get the first domain by its id

        ResponseEntity<DomainResource> getDomainResponse, domainResponseEntity3;
        getDomainResponse = registryClient.getDomainById(domainRequest.getId());
        verifyResponseEntity(getDomainResponse, HttpStatus.OK, true);
        assertThat(getDomainResponse.getBody()).isEqualTo(domainRequest);

        DomainResource domain2 = resourceBuilder.buildDomain("urn:odmp:org.opendatamesh:domains:Domain2");
        registryClient.createDomain(domain2);
        DomainResource domain3 = resourceBuilder.buildDomain("urn:odmp:org.opendatamesh:domains:Domain3");
        domainResponseEntity3 = registryClient.createDomain(domain3);

        getDomainResponse = registryClient.getDomainById(domainResponseEntity3.getBody().getId());
        verifyResponseEntity(getDomainResponse, HttpStatus.OK, true);
        assertThat(getDomainResponse.getBody()).isEqualTo(domainResponseEntity3.getBody());
    }



    // ----------------------------------------
    // UPDATE Domain
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDomainUpdate() throws IOException {
        DomainResource domainRequest = createDomain1();
        domainRequest.setDescription("New description");
        domainRequest.setDisplayName("New Display Name");
        domainRequest.setSummary("New summary");

        ResponseEntity<DomainResource> domainUpdatedResponse = registryClient.updateDomain(domainRequest);
        verifyResponseEntity(domainUpdatedResponse, HttpStatus.OK, true);
        DomainResource domainUpdated = domainUpdatedResponse.getBody();

        assertThat(domainUpdated.getId()).isEqualTo(domainRequest.getId());
        assertThat(domainUpdated.getName()).isEqualTo(domainRequest.getName());
        assertThat(domainUpdated.getSummary()).isEqualTo("New summary");
        assertThat(domainUpdated.getDescription()).isEqualTo("New description");
        assertThat(domainUpdated.getDisplayName()).isEqualTo("New Display Name");
    }



    // ----------------------------------------
    // DELETE Owner
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDomainDelete() throws IOException {

        DomainResource domainRequest = createDomain1();

        ResponseEntity deleteResponse = registryClient.deleteDomain(domainRequest.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);

        ResponseEntity<ErrorRes> getDomainResponse;
        getDomainResponse = registryClient.getDomainById(domainRequest.getId());
        verifyResponseError(getDomainResponse, HttpStatus.NOT_FOUND, RegistryApiStandardErrors.SC404_06_DOMAIN_NOT_FOUND);
    }

    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Domain
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDomainCreateError400Errors() throws IOException {

        ResponseEntity<ErrorRes> errorResponse = null;

        // Test error SC400_16_DOMAIN_IS_EMPTY
        String payload = null;
        errorResponse = registryClient.createDomain(payload);
        verifyResponseError(errorResponse,
                HttpStatus.BAD_REQUEST, RegistryApiStandardErrors.SC400_16_DOMAIN_IS_EMPTY);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDomainCreateError422Errors() throws IOException {

        ResponseEntity<ErrorRes> errorResponse = null;

        DomainResource domainRes = createDomain1();

        // TEST 1: try to register the same domain again
        errorResponse = registryClient.createDomain(domainRes);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_16_DOMAIN_ALREADY_EXISTS);


        // TEST 2: try to register a domain without setting the fqn
        domainRes = resourceBuilder.buildDomain(null, null, null, null, null);
        errorResponse = registryClient.createDomain(domainRes);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_15_DOMAIN_IS_INVALID);
        
        // TEST 3: try to register a domain with an empty fqn
        domainRes = resourceBuilder.buildDomain("    ");
        errorResponse = registryClient.createDomain(domainRes);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_15_DOMAIN_IS_INVALID);
        
    
        // TEST 4: try to register a domain setting an id that not match with the fqn
        domainRes = resourceBuilder.buildDomain("wrong-id", null, null, null, null, null);
        errorResponse = registryClient.createDomain(domainRes);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_15_DOMAIN_IS_INVALID);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDomainUpdate4xxErrors() throws IOException {

        DomainResource domainRes = createDomain1();
        // TEST 1: NULL payload
        ResponseEntity<ErrorRes> errorResponse = registryClient.updateDomain(null);
        verifyResponseError(
                errorResponse,
                HttpStatus.BAD_REQUEST,
                RegistryApiStandardErrors.SC400_16_DOMAIN_IS_EMPTY
        );

        // TEST 2: Empty fqn
        DomainResource updateDomain = resourceBuilder.buildDomain(null);
        errorResponse = registryClient.updateDomain(updateDomain);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_15_DOMAIN_IS_INVALID
        );

        // TEST 3: Not found
        updateDomain.setFullyQualifiedName("test");
        errorResponse = registryClient.updateDomain(updateDomain);
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                RegistryApiStandardErrors.SC404_06_DOMAIN_NOT_FOUND
        );

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDomainGet404Error() throws IOException {
        // TEST: Domain not present
        ResponseEntity<ErrorRes> errorResponse = registryClient.getDomainById("test-id");
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                RegistryApiStandardErrors.SC404_06_DOMAIN_NOT_FOUND
        );
    }

    // ----------------------------------------
    // CREATE Domain
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDomainDelete404Error() throws IOException {
        // TEST: Domain not present
        ResponseEntity<ErrorRes> errorResponse = registryClient.deleteDomain("test-id");
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                RegistryApiStandardErrors.SC404_06_DOMAIN_NOT_FOUND
        );
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDomainDelete409Error() throws IOException {
        DataProductResource createdDataProductRes = resourceBuilder.buildTestDataProduct();
        createdDataProductRes = createDataProduct(createdDataProductRes);

        String descriptorContent = createDataProductVersion(createdDataProductRes.getId(), ODMRegistryTestResources.DPD_CORE);
        DomainResource domainResource = resourceBuilder.buildDomain("urn:odmp:org.opendatamesh:domains:testDomain", "Test Domain", null, null, null);
        ResponseEntity<DomainResource> domainResourceResponse= registryClient.createDomain(domainResource);

        // TEST: Domain not present
        ResponseEntity errorResponse = registryClient.deleteDomain(domainResourceResponse.getBody().getId());
        verifyResponseError(
                errorResponse,
                HttpStatus.CONFLICT,
                RegistryApiStandardErrors.SC409_02_DOMAIN_CAN_NOT_BE_DELETED
        );

        registryClient.deleteDataProduct(createdDataProductRes.getId());

        ResponseEntity okResponse = registryClient.deleteDomain(domainResourceResponse.getBody().getId());
        verifyResponseEntity(
                okResponse,
                HttpStatus.OK,
                false
        );
    }

}