package org.opendatamesh.platform.pp.registry.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryAPIRoutes;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryTestResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DataProductVersionErrorsIT extends ODMRegistryIT {

    // ======================================================================================
    // CREATE Data Product Version
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDPVersionWithMissingPayload() {

        DataProductResource dataProductRes = null;
        dataProductRes = resourceBuilder.buildTestDataProduct();
        dataProductRes = createDataProduct(dataProductRes);

        String payload = null;
        ResponseEntity<ErrorRes> response;
        try {
            response = registryClient.postDataProductVersion(dataProductRes.getId(), payload, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post data product version: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(ODMApiCommonErrors.SC400_00_REQUEST_BODY_IS_NOT_READABLE.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMApiCommonErrors.SC400_00_REQUEST_BODY_IS_NOT_READABLE.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath())
                .isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath() + "/" + dataProductRes.getId() + "/versions");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDPVersionWitEmptyPayload() {

        DataProductResource dataProductRes = null;
        dataProductRes = resourceBuilder.buildTestDataProduct();
        dataProductRes = createDataProduct(dataProductRes);

        String payload = "    ";
        ResponseEntity<ErrorRes> response;
        try {
            response = registryClient.postDataProductVersion(dataProductRes.getId(), payload, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post data product version: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC400_01_DESCRIPTOR_IS_EMPTY.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC400_01_DESCRIPTOR_IS_EMPTY.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo("Input descriptor document cannot be empty");
        assertThat(errorRes.getPath())
                .isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath() + "/" + dataProductRes.getId() + "/versions");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDPVersionWitMalformedPayload() {

        DataProductResource dataProductRes = null;
        dataProductRes = resourceBuilder.buildTestDataProduct();
        dataProductRes = createDataProduct(dataProductRes);

        String payload = "This is an invalid JSON document";
        ResponseEntity<ErrorRes> response;
        try {
            response = registryClient.postDataProductVersion(dataProductRes.getId(), payload, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post data product version: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC422_02_DESCRIPTOR_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_02_DESCRIPTOR_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo("Descriptor document is not a valid JSON document");
        assertThat(errorRes.getPath())
                .isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath() + "/" + dataProductRes.getId() + "/versions");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateSameDPVersionMultipleTimes() {

        DataProductResource dataProductRes = null;
        dataProductRes = resourceBuilder.buildTestDataProduct();
        dataProductRes = createDataProduct(dataProductRes);

        createDataProductVersion(
            dataProductRes.getId(), ODMRegistryTestResources.DPD_CORE);

        String descriptorContent = null;
        try {
            descriptorContent = ODMRegistryTestResources.DPD_CORE.getContent();
        } catch (IOException t) {
            t.printStackTrace();
            fail("Impossible to read data product version from file: " + t.getMessage());
        }

        ResponseEntity<ErrorRes> response = null;
        try {
            response = registryClient.postDataProductVersion(dataProductRes.getId(), descriptorContent, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post data product version: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC422_06_VERSION_ALREADY_EXISTS.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_06_VERSION_ALREADY_EXISTS.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo("Version [1.0.0] of data product [urn:org.opendatamesh:dataproducts:dpdCore] already exists");
        assertThat(errorRes.getPath())
                .isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath() + "/" + dataProductRes.getId() + "/versions");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

   
}