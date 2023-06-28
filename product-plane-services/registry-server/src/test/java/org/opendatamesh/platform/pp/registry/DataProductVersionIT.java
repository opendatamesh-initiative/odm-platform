package org.opendatamesh.platform.pp.registry;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.opendatamesh.platform.core.dpds.model.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.pp.registry.exceptions.OpenDataMeshAPIStandardError;
import org.opendatamesh.platform.pp.registry.resources.v1.DataProductResource;
import org.opendatamesh.platform.pp.registry.resources.v1.ErrorRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.TestPropertySource;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

//@@FixMethodOrder(MethodSorters.JVM)
@TestPropertySource(properties = { "spring.test.context.parallel.enabled=false" })
@Execution(ExecutionMode.SAME_THREAD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DataProductVersionIT extends OpenDataMeshIT {

    // private MockRestServiceServer mockServer;

    InfrastructuralComponentDPDS infrastructuralComponent;

    @Autowired
    ObjectMapper objectMapper;

    @Value("${policyserviceaddress}")
    private String policyserviceaddress;
    @Value("${metaserviceaddress}")
    private String metaserviceaddress;

    // JsonNode descriptorContent;

    @Before
    public void setup() {
        //objectMapper = DataProductDescriptor.buildObjectMapper();
        /*
         * mockServer =
         * MockRestServiceServer.bindTo(restTemplate).ignoreExpectOrder(true).build();
         * try {
         * mockPolicyServicePOST(true);
         * mockMetaServicePOST();
         * } catch (JsonProcessingException e) {
         * logger.error("Impossible to setup mock server", e);
         * }
         * 
         * idDataProductV1 =
         * UUID.nameUUIDFromBytes(fqnDataProductV1.getBytes()).toString();
         * idDataProductV2 =
         * UUID.nameUUIDFromBytes(fqnDataProductV2.getBytes()).toString();
         * fqnDataProductV1InputPort.add(
         * "urn:dpds:it.quantyca:dataproducts:SampleDP:1:inputports:tmsTripCDC");
         * fqnDataProductV1InputPort.add(
         * "urn:dpds:it.quantyca:dataproducts:SampleDP:1:inputports:input-port-2");
         * fqnDataProductV1OutputPort
         * .add("urn:dpds:it.quantyca:dataproducts:SampleDP:1:outputports:output-port-1"
         * );
         * 
         */
    }

    // ======================================================================================
    // HAPPY PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Data product version
    // ----------------------------------------
    @Test
    @Order(1)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionCreation() throws IOException {

        DataProductResource dataProduct1Res = createDataProduct1();
        // Note: we do not do more tests on data product here because they are
        // alredy done in DataProductIT

        String descriptorContent = createDataProduct1Version1(dataProduct1Res.getId());
        System.out.println(descriptorContent);
        verifyBasicContent(descriptorContent);
    }

    // ----------------------------------------
    // READ Data product version
    // ----------------------------------------
    @Test
    @Order(2)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionsReadAll() throws IOException {

        // create a product and associate to it a version
        DataProductResource dataProduct1Res = createDataProduct1();
        String descriptorContent = createDataProduct1Version1(dataProduct1Res.getId());
        JsonNode descriptorRootNode = verifyJsonSynatx(descriptorContent);
        String versionNumber = descriptorRootNode.get("info").get("version").asText();

        // read all version associated to the created data product
        ResponseEntity<String[]> getDataProductVersionsResponse = rest.readAllDataProductVersions(
                dataProduct1Res.getId());
        verifyResponseEntity(getDataProductVersionsResponse, HttpStatus.OK, true);

        // test response
        String[] dataProductVersionNumbers = getDataProductVersionsResponse.getBody();
        assertThat(dataProductVersionNumbers.length).isEqualTo(1);
        assertThat(dataProductVersionNumbers[0]).isEqualTo(versionNumber);
    }

    @Test
    @Order(3)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionsReadOne() throws IOException {

        // create a product and associate to it a version
        DataProductResource dataProduct1Res = createDataProduct1();
        String descriptorContent = createDataProduct1Version1(dataProduct1Res.getId());
        JsonNode descriptorRootNode = verifyJsonSynatx(descriptorContent);
        String versionNumber = descriptorRootNode.get("info").get("version").asText();

        // read the specific version just created
        ResponseEntity<String> getDataProductVersionResponse = rest.readOneDataProductVersion(
                dataProduct1Res.getId(),
                versionNumber);

        // test response
        verifyResponseEntity(getDataProductVersionResponse, HttpStatus.OK, true);
        verifyBasicContent(getDataProductVersionResponse.getBody());
    }

    // ----------------------------------------
    // UPDATE Data product version
    // ----------------------------------------

    // Data product versions are immutable

    // ----------------------------------------
    // Delete Data product version
    // ----------------------------------------

    @Test
    @Order(4)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionDelete()
            throws IOException {

        // create a product and associate to it a version
        DataProductResource dataProduct1Res = createDataProduct1();
        String descriptorContent = createDataProduct1Version1(dataProduct1Res.getId());
        JsonNode descriptorRootNode = verifyJsonSynatx(descriptorContent);
        String versionNumber = descriptorRootNode.get("info").get("version").asText();

        ResponseEntity<String> deleteVersionResponse = rest.deleteDataProductVersion(
                dataProduct1Res.getId(),
                versionNumber);
        verifyResponseEntity(deleteVersionResponse, HttpStatus.OK, false);

        ResponseEntity<String> getOneDataProductVersionResponse = rest.readOneDataProductVersion(
                dataProduct1Res.getId(),
                versionNumber);
        verifyResponseEntity(getOneDataProductVersionResponse, HttpStatus.NOT_FOUND, false);

        ResponseEntity<String[]> getAllDataProductVersionsResponse = rest.readAllDataProductVersions(
                dataProduct1Res.getId());
        verifyResponseEntity(getAllDataProductVersionsResponse, HttpStatus.OK, true);

        String[] dataProductVersionNumbers = getAllDataProductVersionsResponse.getBody();
        assertThat(dataProductVersionNumbers.length).isEqualTo(0);
    }

    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Data product version
    // ----------------------------------------    }

    @Test
    @Order(5)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductCreation400Errors() throws IOException {

        DataProductResource dataProduct1Res = createDataProduct1();

        HttpEntity<String> entity = null;

        // Test error SC400_07_PRODUCT_ID_IS_EMPTY
        // path parameter cannot be null by default. This exception is nerver thrown

        // Test error SC400_01_DESCRIPTOR_IS_EMPTY
        entity = rest.getVersionFileAsHttpEntity(null);
        ResponseEntity<ErrorRes> emptyVersionResponse = rest.postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/{id}/versions"),
                entity,
                ErrorRes.class,
                dataProduct1Res.getId());
        verifyResponseError(emptyVersionResponse,
                HttpStatus.BAD_REQUEST, OpenDataMeshAPIStandardError.SC400_01_DESCRIPTOR_IS_EMPTY);
    }

    @Test
    @Order(6)
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductCreation422Errors() throws IOException {

        ResponseEntity<ErrorRes> errorResponse;
        HttpEntity<String> entity = null;

        // create the product
        DataProductResource dataProduct1Res = createDataProduct1();

        // Test error SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID

        entity = rest.getVersionFileAsHttpEntity(RESOURCE_DP1_V1);

        // remove from descriptor fullyQualifiedName property
        String descriptorContent = entity.getBody();
        JsonNode descriptorRootEntity = mapper.readTree(descriptorContent);
        ObjectNode infoObject = (ObjectNode) descriptorRootEntity.get("info");
        infoObject.remove("fullyQualifiedName");
        entity = new HttpEntity<String>(mapper.writeValueAsString(descriptorRootEntity), entity.getHeaders());

        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/{id}/versions"),
                entity,
                ErrorRes.class,
                dataProduct1Res.getId());
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID);

        // Test error SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID
        entity = new HttpEntity<String>("This is an invalid JSON document", entity.getHeaders());
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/{id}/versions"),
                entity,
                ErrorRes.class,
                dataProduct1Res.getId());
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID);

        // Test error SC422_05_VERSION_ALREADY_EXISTS

        // associate the version to the product
        String str = createDataProduct1Version1(dataProduct1Res.getId());

        // and the re associate it
        entity = rest.getVersionFileAsHttpEntity(RESOURCE_DP1_V1);
        errorResponse = rest.postForEntity(
                apiUrl(RoutesV1.DATA_PRODUCTS, "/{id}/versions"),
                entity,
                ErrorRes.class,
                dataProduct1Res.getId());

        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                OpenDataMeshAPIStandardError.SC422_05_VERSION_ALREADY_EXISTS);
    }

    // ======================================================================================
    // PRIVATE METHODS
    // ======================================================================================

    // ----------------------------------------
    // Create test resources
    // ----------------------------------------

    // TODO ...as needed

    // ----------------------------------------
    // Verify test resources
    // ----------------------------------------

    private JsonNode verifyBasicContent(String responseBody) {

        JsonNode rootEntity = verifyJsonSynatx(responseBody);

        // test dataProductDescriptor
        assertThat(rootEntity.get("dataProductDescriptor").asText())
                .isEqualTo("1.0.0");

        // test info object
        JsonNode infoObject = rootEntity.path("info");
        assertThat(infoObject).isNotNull();

        assertThat(infoObject.get("id")).isNotNull();

        assertThat(infoObject.get("entityType")).isNotNull();
        assertThat(infoObject.get("entityType").asText())
                .isEqualTo("dataproduct");

        assertThat(infoObject.get("x-prop")).isNotNull();
        assertThat(infoObject.get("x-prop").asText())
                .isEqualTo("custom-prop-value");

        // test interface components
        JsonNode interfaceComponentsObject = rootEntity.path("interfaceComponents");
        assertThat(interfaceComponentsObject).isNotNull();

        JsonNode inputPorts = interfaceComponentsObject.path("inputPorts");
        assertThat(inputPorts).isNotNull();
        assertThat(inputPorts.isArray()).isTrue();
        assertThat(inputPorts.size()).isEqualTo(2);
        for (JsonNode inputPort : inputPorts) {
            assertThat(inputPort.get("id"))
                    .isNotNull();
            assertThat(inputPort.get("entityType").asText())
                    .isEqualTo("inputport");
            assertThat(inputPort.get("x-prop").asText())
                    .isEqualTo("custom-prop-value");

            ObjectNode apiObject = (ObjectNode) inputPort.at("/promises/api");
            assertThat(apiObject.get("specification")).isNotNull();
            assertThat(apiObject.get("specification").asText()).isEqualTo("custom-api-spec");
            ObjectNode apiDefinitionObject = (ObjectNode) apiObject.get("definition");
            assertThat(apiDefinitionObject.size()).isEqualTo(1);
            assertThat(apiDefinitionObject.get("$ref")).isNotNull();

            /*
             * ResponseEntity<String> getApiDefinitionResponse = rest.getForEntity(
             * apiDefinitionObject.get("$ref").asText(), String.class
             * );
             * assertThat(getApiDefinitionResponse.getStatusCode())
             * .isEqualByComparingTo(HttpStatus.OK);
             * assertThat(getApiDefinitionResponse.getBody())
             * .isNotNull();
             */

        }

        JsonNode outputPorts = interfaceComponentsObject.path("outputPorts");
        assertThat(outputPorts).isNotNull();
        assertThat(outputPorts.isArray()).isTrue();
        assertThat(outputPorts.size()).isEqualTo(1);
        for (JsonNode outputPort : outputPorts) {
            assertThat(outputPort.get("id"))
                    .isNotNull();
            assertThat(outputPort.get("entityType").asText())
                    .isEqualTo("outputport");
            assertThat(outputPort.get("x-prop").asText())
                    .isEqualTo("custom-prop-value");

            ObjectNode apiObject = (ObjectNode) outputPort.at("/promises/api");
            assertThat(apiObject.get("specification")).isNotNull();
            assertThat(apiObject.get("specification").asText()).isEqualTo("custom-api-spec");
            ObjectNode apiDefinitionObject = (ObjectNode) apiObject.get("definition");
            assertThat(apiDefinitionObject.size()).isEqualTo(1);
            assertThat(apiDefinitionObject.get("$ref")).isNotNull();
        }

        // test internal components
        JsonNode internalComponentsObject = rootEntity.path("internalComponents");
        assertThat(internalComponentsObject).isNotNull();

        // test application components
        JsonNode appComponents = internalComponentsObject.path("applicationComponents");
        assertThat(appComponents).isNotNull();
        assertThat(appComponents.isArray()).isTrue();
        assertThat(appComponents.size()).isEqualTo(1);
        for (JsonNode appComponent : appComponents) {
            assertThat(appComponent.get("id"))
                    .isNotNull();
            assertThat(appComponent.get("entityType").asText())
                    .isEqualTo("application");
            assertThat(appComponent.get("x-prop").asText())
                    .isEqualTo("custom-prop-value");
        }

        // test application components
        JsonNode infraComponents = internalComponentsObject.path("infrastructuralComponents");
        assertThat(infraComponents).isNotNull();
        assertThat(infraComponents.isArray()).isTrue();
        assertThat(infraComponents.size()).isEqualTo(1);
        for (JsonNode infraComponent : infraComponents) {
            assertThat(infraComponent.get("id"))
                    .isNotNull();
            assertThat(infraComponent.get("entityType").asText())
                    .isEqualTo("infrastructure");
            assertThat(infraComponent.get("x-prop").asText())
                    .isEqualTo("custom-prop-value");
        }

        return rootEntity;
    }
}