package org.opendatamesh.platform.pp.registry.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
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
public class ApiIT extends ODMRegistryIT {

    // ======================================================================================
    // CREATE Api
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateApiWithAllProperties() throws IOException {

        ExternalComponentResource api = null, api1 = null;

        api = resourceBuilder.buildTestApi();

        api1 = createApi(api);

        assertThat(api1.getId()).isEqualTo(api.getId());
        assertThat(api1.getEntityType()).isEqualTo(api.getEntityType());
        assertThat(api1.getName()).isEqualTo(api.getName());
        assertThat(api1.getVersion()).isEqualTo(api.getVersion());
        assertThat(api1.getDisplayName()).isEqualTo(api.getDisplayName());
        assertThat(api1.getDescription()).isEqualTo(api.getDescription());
        assertThat(api1.getSpecification()).isEqualTo(api.getSpecification());
        assertThat(api1.getSpecificationVersion()).isEqualTo(api.getSpecificationVersion());
        assertThat(api1.getDefinitionMediaType()).isEqualTo(api.getDefinitionMediaType());
        assertThat(api1.getDefinition()).isEqualTo(api.getDefinition());
    }

    // ======================================================================================
    // READ Api
    // ======================================================================================
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadApis() {

        ExternalComponentResource api = null, api1 = null, api2 = null, api3 = null, api4 = null;

        api = resourceBuilder.buildTestApi();
        api1 = createApi(api);
        assertThat(api1).usingRecursiveComparison()
                .ignoringFields("fullyQualifiedName", "createdAt", "updatedAt")
                .isEqualTo(api);

        api.setVersion("2.0.0");
        api2 = createApi(api);
        assertThat(api2).isNotNull();
        assertThat(api2.getVersion()).isEqualTo(api.getVersion());

        api.setVersion("3.0.0");
        api3 = createApi(api);
        assertThat(api3).isNotNull();
        assertThat(api3.getVersion()).isEqualTo(api.getVersion());

        api.setName("api-2");
        api4 = createApi(api);
        assertThat(api4).isNotNull();
        assertThat(api4.getName()).isEqualTo(api.getName());

        ResponseEntity<ExternalComponentResource[]> response = registryClient.getApis();
        verifyResponseEntity(response, HttpStatus.OK, true);

        ExternalComponentResource[] definitionResources = response.getBody();
        assertThat(definitionResources.length).isEqualTo(4);

        assertThat(definitionResources[0].getDefinition()).isEqualTo(api.getDefinition());
        assertThat(definitionResources[1].getDefinition()).isEqualTo(api.getDefinition());
        assertThat(definitionResources[2].getDefinition()).isEqualTo(api.getDefinition());
        assertThat(definitionResources[3].getDefinition()).isEqualTo(api.getDefinition());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadApi() {

        ExternalComponentResource api = null, api1 = null, api2 = null, api3 = null, api4 = null;

        api = resourceBuilder.buildTestApi();
        api1 = createApi(api);

        api.setVersion("2.0.0");
        api2 = createApi(api);

        api.setVersion("3.0.0");
        api3 = createApi(api);

        api.setName("api-2");
        api4 = createApi(api);

        ResponseEntity<ExternalComponentResource> response = null;

        response = registryClient.getApi(api1.getId());
        verifyResponseEntity(response, HttpStatus.OK, true);
        assertThat(response.getBody()).isEqualTo(api1);

        response = registryClient.getApi(api2.getId());
        verifyResponseEntity(response, HttpStatus.OK, true);
        assertThat(response.getBody()).isEqualTo(api2);

        response = registryClient.getApi(api3.getId());
        verifyResponseEntity(response, HttpStatus.OK, true);
        assertThat(response.getBody()).isEqualTo(api3);

        response = registryClient.getApi(api4.getId());
        verifyResponseEntity(response, HttpStatus.OK, true);
        assertThat(response.getBody()).isEqualTo(api4);

    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchApis() {

        ExternalComponentResource api = null, api1 = null, api2 = null, api3 = null, api4 = null;

        api = resourceBuilder.buildTestApi();
        api1 = createApi(api);

        api.setVersion("2.0.0");
        api2 = createApi(api);

        api.setVersion("3.0.0");
        api3 = createApi(api);

        api.setName("api-2");
        api4 = createApi(api);

        Optional<String> name = Optional.ofNullable(null);
        Optional<String> version = Optional.of("3.0.0");
        Optional<String> specification = Optional.ofNullable(null);
        Optional<String> specificationVersion = Optional.ofNullable(null);

        ResponseEntity<ExternalComponentResource[]> getDefinitionResponse = registryClient.searchApis(
                name,
                version,
                specification,
                specificationVersion);
        ExternalComponentResource[] definitionResources = getDefinitionResponse.getBody();
        verifyResponseEntity(getDefinitionResponse, HttpStatus.OK, true);

        assertThat(getDefinitionResponse.getBody().length).isEqualTo(2);
        assertThat(definitionResources[0].getDefinition()).isEqualTo(api3.getDefinition());
        assertThat(definitionResources[1].getDefinition()).isEqualTo(api4.getDefinition());

        // TODO try more combination of search parameters and verify response
    }

    // ======================================================================================
    // UPDATE Api
    // ======================================================================================

    // Api are immutable

    // ======================================================================================
    // DELETE Api
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDeleteApi() throws IOException {

        ResponseEntity<ExternalComponentResource> response = null;

       ExternalComponentResource api = null, api1 = null, api2 = null, api3 = null, api4 = null;

        api = resourceBuilder.buildTestApi();
        api1 = createApi(api);

        api.setVersion("2.0.0");
        api2 = createApi(api);

        api.setVersion("3.0.0");
        api3 = createApi(api);

        api.setName("api-2");
        api4 = createApi(api);

        try {
            response = registryClient.deleteOneApi(api2.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to delete api definition: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ExternalComponentResource[] apis;
        try {
            apis = registryClient.readAllApis();
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to delete api definition: " + t.getMessage());
            return;
        }

        assertThat(apis).isNotNull();
        assertThat(apis.length).isEqualTo(3);

        ResponseEntity<ErrorRes> errorResponse = null;
        try {
            errorResponse = registryClient.getApi(api2.getId(), ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to delete api definition: " + t.getMessage());
            return;
        }

        assertThat(errorResponse).isNotNull();
        assertThat(errorResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

    }
}