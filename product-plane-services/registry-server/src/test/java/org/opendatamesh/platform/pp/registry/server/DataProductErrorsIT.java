package org.opendatamesh.platform.pp.registry.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryAPIRoutes;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

//TODO every update to data product must check and mock the call to the policyservice

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DataProductErrorsIT extends ODMRegistryIT {

    // ======================================================================================
    // CREATE Data Product
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDataProductWithMissingPayload() {

        ResponseEntity<ErrorRes> response = null;
        try {
            response = registryClient.postDataProduct(null, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post data product: " + t.getMessage());
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
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDataProductWithEmptyPayload() {

        ResponseEntity<ErrorRes> response = null;
        try {
            response = registryClient.postDataProduct("    ", ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post data product: " + t.getMessage());
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
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDataProductWithMissingFqn() {

        ResponseEntity<ErrorRes> errorResponse = null;

        DataProductResource dataProductRes = resourceBuilder.buildDataProduct(null, "marketing", "marketing product");
        ;
        try {
            errorResponse = registryClient.postDataProduct(dataProductRes, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = errorResponse.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo("Data product fullyQualifiedName property cannot be empty");
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDataProductWithEmptyFqn() {

        ResponseEntity<ErrorRes> errorResponse = null;

        DataProductResource dataProductRes = resourceBuilder.buildDataProduct("    ", "marketing", "marketing product");
        ;
        try {
            errorResponse = registryClient.postDataProduct(dataProductRes, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = errorResponse.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo("Data product fullyQualifiedName property cannot be empty");
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDataProductWithWrongId() {

        ResponseEntity<ErrorRes> errorResponse = null;

        DataProductResource dataProductRes = resourceBuilder.buildDataProduct("wrong-id", "prod-5", "marketing",
                "marketing product");
        try {
            errorResponse = registryClient.postDataProduct(dataProductRes, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = errorResponse.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).startsWith("Data product [prod-5] with id [wrong-id] is invalid.");
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateSameDataProductMultipleTimes() {

        ResponseEntity<ErrorRes> errorResponse = null;

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildDataProduct(
            "a9228eb7-3179-3628-ae64-aa5dbb1fcb28", 
            "urn:org.opendatamesh:dataproducts:testProduct", 
            "Test Domain",
            "This is test product #1");
        createdDataProductRes = createDataProduct(createdDataProductRes);

        try {
            errorResponse = registryClient.postDataProduct(createdDataProductRes, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post data product: " + t.getMessage());
            return;
        }

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = errorResponse.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(RegistryApiStandardErrors.SC422_04_PRODUCT_ALREADY_EXISTS.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_04_PRODUCT_ALREADY_EXISTS.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo(
                "Data product [" + createdDataProductRes.getFullyQualifiedName() + "] already exists");
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }
    
    // ======================================================================================
    // READ Data Products
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadNotExistingDataProduct() {

        ResponseEntity<ErrorRes> errorResponse = null;

        try {
            errorResponse = registryClient.getDataProduct("wrong-id", ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible read data product: " + t.getMessage());
            return;
        }

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorRes errorRes = errorResponse.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo(
            "Data Product with id [wrong-id] does not exist");
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath() + "/wrong-id");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    // ======================================================================================
    // UPDATE Data Product
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUpdateDataProductWithMissingPayload() {

        ResponseEntity<ErrorRes> response = null;
        try {
            response = registryClient.putDataProduct(null, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post data product: " + t.getMessage());
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
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUpdateDataProductWithMissingIdAndFqn() {

        ResponseEntity<ErrorRes> errorResponse = null;

        DataProductResource dataProductRes = resourceBuilder.buildDataProduct(null, null, "marketing", "marketing product");
        try {
            errorResponse = registryClient.putDataProduct(dataProductRes, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = errorResponse.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo("Data product id and fullyQualifiedName properties cannot be both empty");
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUpdateDataProductWithEmptyIdAndFqn() {

        ResponseEntity<ErrorRes> errorResponse = null;

        DataProductResource dataProductRes = resourceBuilder.buildDataProduct("   ", "   ", "marketing", "marketing product");
        try {
            errorResponse = registryClient.putDataProduct(dataProductRes, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = errorResponse.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo("Data product id and fullyQualifiedName properties cannot be both empty");
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUpdateDataProductWithWrongId() {

        ResponseEntity<ErrorRes> errorResponse = null;

        DataProductResource dataProductRes = resourceBuilder.buildDataProduct("id", "fqn", "marketing", "marketing product");
        try {
            errorResponse = registryClient.putDataProduct(dataProductRes, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to update data product: " + t.getMessage());
            return;
        }

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = errorResponse.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_05_PRODUCT_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).startsWith("Data product id [id] does not match with fullyQualifiedName [fqn].");
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUpdateNotExistingDataProduct() {

        ResponseEntity<ErrorRes> errorResponse = null;

        DataProductResource dataProductRes = resourceBuilder.buildDataProduct(
            "a9228eb7-3179-3628-ae64-aa5dbb1fcb28", 
            "urn:org.opendatamesh:dataproducts:testProduct", 
            "marketing", "marketing product");
        try {
            errorResponse = registryClient.putDataProduct(dataProductRes, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to update data product: " + t.getMessage());
            return;
        }

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorRes errorRes = errorResponse.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).startsWith("Data product [urn:org.opendatamesh:dataproducts:testProduct] with id [a9228eb7-3179-3628-ae64-aa5dbb1fcb28] doesn't exists");
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    // ======================================================================================
    // DELETE Data Product
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDeleteNotExistingDataProduct() {

        ResponseEntity<ErrorRes> errorResponse = null;

        try {
            errorResponse = registryClient.deleteOneDataProduct("wrong-id", ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible read data product: " + t.getMessage());
            return;
        }

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorRes errorRes = errorResponse.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC404_01_PRODUCT_NOT_FOUND.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo(
            "Data Product with id [wrong-id] does not exist");
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.DATA_PRODUCTS.getPath() + "/wrong-id");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }
}