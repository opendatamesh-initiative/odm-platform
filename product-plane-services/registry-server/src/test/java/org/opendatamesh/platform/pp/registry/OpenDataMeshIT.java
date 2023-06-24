package org.opendatamesh.platform.pp.registry;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.runner.RunWith;
import org.opendatamesh.platform.pp.registry.database.entities.sharedres.Definition;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.DataProductResource;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.DefaultUriBuilderFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { OpenDataMeshApp.class })
public abstract class OpenDataMeshIT {

    @LocalServerPort
    protected String port;

    protected OpenDataMeshITRestTemplate rest;

    protected Logger logger = LoggerFactory.getLogger("OpenDataMeshIT.class");

    protected final String RESOURCE_DP1 = "src/test/resources/test/dataproduct-descriptor/dp1.json";
    protected final String RESOURCE_DP1_UPD = "src/test/resources/test/dataproduct-descriptor/dp1-updated.json";
    protected final String RESOURCE_DP1_V1 = "src/test/resources/test/dataproduct-descriptor/dp1-v1.json";
    protected final String RESOURCE_DP1_V1_API1 = "src/test/resources/test/dataproduct-descriptor/dp1-v1-api1.json";
    protected final String RESOURCE_DEF1_V1 = "src/test/resources/test/definition/def1.json";
    protected final String RESOURCE_DEF1_NOVERSION = "src/test/resources/test/definition/def1-missing-version.json";
    protected final String RESOURCE_DEF1_NONAME = "src/test/resources/test/definition/def1-missing-name.json";
    protected final String RESOURCE_DEF1_NONAME_NOVERSION = "src/test/resources/test/definition/def2-missing-name-version.json";

    protected final String RESOURCE_DPS_URI = "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json";
        
    @Autowired
    protected ObjectMapper mapper;

    @PostConstruct
    public final void init() {
        rest = new OpenDataMeshITRestTemplate();
        rest.setPort(port);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        // requestFactory.setConnectTimeout(30000);
        // requestFactory.setReadTimeout(30000);
        rest.getRestTemplate().setRequestFactory(requestFactory);
        // add uri template handler because '+' of iso date would not be encoded
        DefaultUriBuilderFactory defaultUriBuilderFactory = new DefaultUriBuilderFactory();
        defaultUriBuilderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.TEMPLATE_AND_VALUES);
        rest.setUriTemplateHandler(defaultUriBuilderFactory);
    }

    // ======================================================================================
    // Url builder utils
    // ======================================================================================
    protected String apiUrl(RoutesV1 route) {
        return apiUrl(route, "");
    }

    protected String apiUrl(RoutesV1 route, String extension) {
        return apiUrlFromString(route.getPath() + extension);
    }

    protected String apiUrlFromString(String routeUrlString) {
        return "http://localhost:" + port + routeUrlString;
    }

    protected String apiUrlOfItem(RoutesV1 route) {
        return apiUrl(route, "/{id}");
    }

    // ======================================================================================
    // Create test basic resources
    // ======================================================================================

    protected DataProductResource createDataProduct1() throws IOException {
        ResponseEntity<DataProductResource> postProductResponse = rest.createDataProduct(RESOURCE_DP1);
        verifyResponseEntity(postProductResponse, HttpStatus.CREATED, true);

        return postProductResponse.getBody();

    }

    protected DataProductResource updateDataProduct1() throws IOException {
        ResponseEntity<DataProductResource> putProductResponse = rest.updateDataProduct(RESOURCE_DP1_UPD);
        verifyResponseEntity(putProductResponse, HttpStatus.OK, true);

        return putProductResponse.getBody();

    }

    protected String createDataProduct1Version1(String dataProduct1Id) throws IOException {
        ResponseEntity<String> postProductVersionResponse = rest.createDataProductVersion(
                dataProduct1Id, RESOURCE_DP1_V1);
        verifyResponseEntity(postProductVersionResponse, HttpStatus.CREATED, true);

        return postProductVersionResponse.getBody();
    }

    protected String uploadDataProductVersion() throws IOException {
        ResponseEntity<String> uploadProductVersionResponse = rest.uploadDataProductVersion(
            RESOURCE_DPS_URI);
        verifyResponseEntity(uploadProductVersionResponse, HttpStatus.CREATED, true);

        return uploadProductVersionResponse.getBody();
    }

    protected Definition createDataProduct1Version1Api1() throws IOException {
        ResponseEntity<Definition> postProductVersionApiResponse = rest.createDefinition(
                RESOURCE_DP1_V1);
        verifyResponseEntity(postProductVersionApiResponse, HttpStatus.CREATED, true);

        return postProductVersionApiResponse.getBody();
    }

    

    protected Definition createDefinition1() throws IOException {
        ResponseEntity<Definition> postDefinition = rest.createDefinition(
                RESOURCE_DEF1_V1
        );
        verifyResponseEntity(postDefinition, HttpStatus.CREATED, true);

        return postDefinition.getBody();
    }

    protected Definition createDefinitionMissingVersion() throws IOException {
        ResponseEntity<Definition> postDefinition = rest.createDefinition(
                RESOURCE_DEF1_NOVERSION
        );
        verifyResponseEntity(postDefinition, HttpStatus.CREATED, true);

        return postDefinition.getBody();
    }

    protected Definition createDefinitionMissingName() throws IOException {
        ResponseEntity<Definition> postDefinition = rest.createDefinition(
                RESOURCE_DEF1_NONAME
        );
        verifyResponseEntity(postDefinition, HttpStatus.CREATED, true);

        return postDefinition.getBody();
    }

    protected Definition createDefinitionMissingNameAndVersion() throws IOException {
        ResponseEntity<Definition> postDefinition = rest.createDefinition(
                RESOURCE_DEF1_NONAME_NOVERSION
        );
        verifyResponseEntity(postDefinition, HttpStatus.CREATED, true);

        return postDefinition.getBody();
    }

    // ======================================================================================
    // Verify test basic resources
    // ======================================================================================

    protected JsonNode verifyJsonSynatx(String responseBody) {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        JsonNode rootEntity = null;
        try {
            rootEntity = mapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            fail("Impossible to parse response");
        }
        return rootEntity;
    }

    protected ResponseEntity verifyResponseEntity(ResponseEntity responseEntity, HttpStatus statusCode,
            boolean checkBody) {
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(statusCode);
        if (checkBody) {
            assertThat(responseEntity.getBody()).isNotNull();
        }
        return responseEntity;
    }

    protected void verifyResponseError(
            ResponseEntity<ErrorRes> errorResponse,
            HttpStatus status,
            OpenDataMeshAPIStandardError error) {
        assertThat(errorResponse.getStatusCode())
                .isEqualByComparingTo(status);
        assertThat(errorResponse.getBody().getCode())
                .isEqualTo(error.code());
        assertThat(errorResponse.getBody().getDescription())
                .isEqualTo(error.description());
    }

    // ======================================================================================
    // Verify reresponse
    // ======================================================================================

    // TODO ...add as needed
}
