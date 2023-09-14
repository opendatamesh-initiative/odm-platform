package org.opendatamesh.platform.pp.registry.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.core.dpds.model.core.EntityTypeDPDS;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryAPIRoutes;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.server.database.entities.Api;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryTestResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ApiErrorsIT extends ODMRegistryIT {

    // ======================================================================================
    // CREATE Api
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateApiWithMissingPayload() {

        ResponseEntity<ErrorRes> response = null;
        try {
            response = registryClient.postApi(null, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post api definition: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC400_08_API_IS_EMPTY.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC400_08_API_IS_EMPTY.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.APIS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateApiWithEmptyPayload() {

        ResponseEntity<ErrorRes> response = null;
        try {
            response = registryClient.postApi("    ", ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post api definition: " + t.getMessage());
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
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.APIS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateApiWithMissingName() throws IOException {

        ResponseEntity<ErrorRes> response = null;

        ExternalComponentResource api = resourceBuilder.buildDefinition(
            null, null, EntityTypeDPDS.API.propertyValue(), 
            "definition1", "0.0.1", 
            "testdef1", "test definition", 
            "spec", "1.0", 
            "plain/text", "Content of test definition");
        
        api.setName(null);

        try {
            response = registryClient.postApi(api, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post api definition: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC422_08_API_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_08_API_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.APIS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }


    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateApiWithMissingVersion() throws IOException {

        ResponseEntity<ErrorRes> response = null;

        ExternalComponentResource api = resourceBuilder.buildDefinition(
            null, null, EntityTypeDPDS.API.propertyValue(), 
            "definition1", "0.0.1", 
            "testdef1", "test definition", 
            "spec", "1.0", 
            "plain/text", "Content of test definition");
        
        api.setVersion(null);

        try {
            response = registryClient.postApi(api, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post api definition: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC422_08_API_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_08_API_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.APIS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateApiWithMissingDefinition() throws IOException {

        ResponseEntity<ErrorRes> response = null;

        ExternalComponentResource api = resourceBuilder.buildDefinition(
            null, null, EntityTypeDPDS.API.propertyValue(), 
            "definition1", "0.0.1", 
            "testdef1", "test definition", 
            "spec", "1.0", 
            "plain/text", "Content of test definition");
        
        api.setDefinition(null);

        try {
            response = registryClient.postApi(api, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post api definition: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC422_08_API_NOT_VALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_08_API_NOT_VALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.APIS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateSameApiMultipleTime() {

        ResponseEntity<ErrorRes> response = null;

        ExternalComponentResource api = resourceBuilder.buildDefinition(
            null, null, EntityTypeDPDS.API.propertyValue(), 
            "definition1", "0.0.1", 
            "testdef1", "test definition", 
            "spec", "1.0", 
            "plain/text", "Content of test definition");
        

        try {
            registryClient.createApi(api);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post api definition: " + t.getMessage());
            return;
        }

        try {
            response = registryClient.postApi(api, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post api definition: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC422_07_API_ALREADY_EXISTS.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC422_07_API_ALREADY_EXISTS.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.APIS.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }
   
    // ----------------------------------------
    // DELETE Definition
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDeleteNotExistingApi() {
        ResponseEntity<ErrorRes> response = null;

        ExternalComponentResource api = null, api1 = null, api2 = null, api3 = null;
        
        api = resourceBuilder.buildDefinition(
            null, null, EntityTypeDPDS.API.propertyValue(), 
            "definition1", "0.0.1", 
            "testdef1", "test definition", 
            "spec", "1.0", 
            "plain/text", "Content of test definition");
        

        try {
            api.setName("api-1");
            api1 = registryClient.createApi(api);

            api.setName("api-2");
            api2 = registryClient.createApi(api);

            api.setName("api-3");
            api3 = registryClient.createApi(api);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post api definitions: " + t.getMessage());
            return;
        }

        try {
            response = registryClient.deleteApi("wrong-id", ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to delete api definition: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(RegistryApiStandardErrors.SC404_03_API_NOT_FOUND.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(RegistryApiStandardErrors.SC404_03_API_NOT_FOUND.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(RegistryAPIRoutes.APIS.getPath() + "/wrong-id");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorRes.getTimestamp()).isNotNull();    
    }
}