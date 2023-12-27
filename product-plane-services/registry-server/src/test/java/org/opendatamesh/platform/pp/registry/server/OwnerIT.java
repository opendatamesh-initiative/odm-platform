package org.opendatamesh.platform.pp.registry.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.OwnerResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryTestResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class OwnerIT extends ODMRegistryIT {

    // ======================================================================================
    // HAPPY PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Owner
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testOwnerCreate()
    throws IOException {

        OwnerResource ownerResource;

        // TEST 1: create first Owner
        OwnerResource ownerResourceResponse = createOwner1();
        assertThat(ownerResourceResponse.getId()).isEqualTo("test@test.it");
        assertThat(ownerResourceResponse.getName()).isEqualTo("IT Department");

    }

    // ----------------------------------------
    // READ Owner
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testOwnerReadAll() throws IOException {

        // create first owner
        createOwner1();

        // TEST 1: verify first owner

        ResponseEntity<OwnerResource[]> getOwnerResponse = null;
        getOwnerResponse = registryClient.readAllOwners();
        verifyResponseEntity(getOwnerResponse, HttpStatus.OK, true);
        assertThat(getOwnerResponse.getBody().length).isEqualTo(1);

        OwnerResource ownerResource2 = resourceBuilder.buildOwner("test2@test.it", "IT Department");
        registryClient.createOwner(ownerResource2);


        // TEST 2: verify second Owner

        getOwnerResponse = registryClient.readAllOwners();
        verifyResponseEntity(getOwnerResponse, HttpStatus.OK, true);
        assertThat(getOwnerResponse.getBody().length).isEqualTo(2);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testOwnerGetOneById() throws IOException {

        OwnerResource ownerResource = createOwner1();

        // TEST 1: get the first Owner by its id

        ResponseEntity<OwnerResource> getOwnerResponse, ownerResponseEntity3;
        getOwnerResponse = registryClient.getOwnerById(ownerResource.getId());
        verifyResponseEntity(getOwnerResponse, HttpStatus.OK, true);
        assertThat(getOwnerResponse.getBody()).isEqualTo(ownerResource);

        OwnerResource owner2 = resourceBuilder.buildOwner("test2@test.it", "IT Department");
        registryClient.createOwner(owner2);
        OwnerResource owner3 = resourceBuilder.buildOwner("test3@test.it", "IT Department");
        ownerResponseEntity3 = registryClient.createOwner(owner3);

        getOwnerResponse = registryClient.getOwnerById(ownerResponseEntity3.getBody().getId());
        verifyResponseEntity(getOwnerResponse, HttpStatus.OK, true);
        assertThat(getOwnerResponse.getBody()).isEqualTo(ownerResponseEntity3.getBody());
    }



    // ----------------------------------------
    // UPDATE Owner
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testOwnerUpdate() throws IOException {
        OwnerResource ownerResource = createOwner1();

        ownerResource.setName("new name");

        ResponseEntity<OwnerResource> ownerUpdatedResponse = registryClient.updateOwner(ownerResource);
        verifyResponseEntity(ownerUpdatedResponse, HttpStatus.OK, true);
        OwnerResource ownerUpdated = ownerUpdatedResponse.getBody();

        assertThat(ownerUpdated.getId()).isEqualTo(ownerResource.getId());
        assertThat(ownerUpdated.getName()).isEqualTo(ownerResource.getName());
    }



    // ----------------------------------------
    // DELETE Owner
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testOwnerDelete() throws IOException {

        OwnerResource ownerResource = createOwner1();

        ResponseEntity deleteResponse = registryClient.deleteOwner(ownerResource.getId());
        verifyResponseEntity(deleteResponse, HttpStatus.OK, false);
    }

    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Owner
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testOwnerCreateError400Errors() throws IOException {

        ResponseEntity<ErrorRes> errorResponse = null;

        // Test error SC400_17_OWNER_IS_EMPTY
        String payload = null;
        errorResponse = registryClient.createOwner(payload);
        verifyResponseError(errorResponse,
                HttpStatus.BAD_REQUEST, RegistryApiStandardErrors.SC400_17_OWNER_IS_EMPTY);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testOwnerCreateError422Errors() throws IOException {

        ResponseEntity<ErrorRes> errorResponse = null;

        OwnerResource ownerResource = createOwner1();

        // TEST 1: try to register the same owner again
        errorResponse = registryClient.createOwner(ownerResource);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_18_OWNER_ALREADY_EXISTS);


        // TEST 2: try to register a owner without setting the id
        ownerResource = resourceBuilder.buildOwner(null, null);
        errorResponse = registryClient.createOwner(ownerResource);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_17_OWNER_IS_INVALID);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testOwnerUpdate4xxErrors() throws IOException {

        OwnerResource ownerResource = createOwner1();
        // TEST 1: NULL payload
        ResponseEntity<ErrorRes> errorResponse = registryClient.updateOwner(null);
        verifyResponseError(
                errorResponse,
                HttpStatus.BAD_REQUEST,
                RegistryApiStandardErrors.SC400_17_OWNER_IS_EMPTY
        );

        // TEST 2: Empty id
        OwnerResource updateOwner = resourceBuilder.buildOwner(null, null);
        errorResponse = registryClient.updateOwner(updateOwner);
        verifyResponseError(
                errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_17_OWNER_IS_INVALID
        );

        // TEST 3: Not found
        updateOwner.setId("test");
        errorResponse = registryClient.updateOwner(updateOwner);
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                RegistryApiStandardErrors.SC404_07_OWNER_NOT_FOUND
        );

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testOwnerGet404Error() throws IOException {
        // TEST: Owner not present
        ResponseEntity<ErrorRes> errorResponse = registryClient.getOwnerById("test-id");
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                RegistryApiStandardErrors.SC404_07_OWNER_NOT_FOUND
        );
    }

    // ----------------------------------------
    // DELETE Owner
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testOwnerDelete404Error() throws IOException {
        // TEST: Owner not present
        ResponseEntity<ErrorRes> errorResponse = registryClient.deleteOwner("test-id");
        verifyResponseError(
                errorResponse,
                HttpStatus.NOT_FOUND,
                RegistryApiStandardErrors.SC404_07_OWNER_NOT_FOUND
        );
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testOwnerDelete409Error() throws IOException {
        DataProductResource createdDataProductRes = resourceBuilder.buildTestDataProduct();
        createdDataProductRes = createDataProduct(createdDataProductRes);

        String descriptorContent = createDataProductVersion(createdDataProductRes.getId(), ODMRegistryTestResources.DPD_CORE);

        // TEST: Owner not present
        ResponseEntity errorResponse = registryClient.deleteOwner("john.doe@company-xyz.com");
        verifyResponseError(
                errorResponse,
                HttpStatus.CONFLICT,
                RegistryApiStandardErrors.SC409_03_OWNER_CAN_NOT_BE_DELETED
        );

        registryClient.deleteDataProduct(createdDataProductRes.getId());

        ResponseEntity okResponse = registryClient.deleteOwner("john.doe@company-xyz.com");
        verifyResponseEntity(
                okResponse,
                HttpStatus.OK,
                false
        );
    }

}