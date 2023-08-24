package org.opendatamesh.platform.pp.registry.server;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.EnabledIf;

import java.io.IOException;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@TestPropertySource(properties = { "spring.test.context.parallel.enabled=false" })
@Execution(ExecutionMode.SAME_THREAD)
@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DataProductVersionIT extends ODMRegistryIT {

    @Before
    public void setup() {
        // objectMapper = DataProductDescriptor.buildObjectMapper();
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
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionCreation() throws IOException {

        DataProductResource dataProduct1Res = createDataProduct(ODMRegistryResources.DP1);
        // Note: we do not do more tests on data product here because they are
        // alredy done in DataProductIT

        String descriptorContent = createDataProductVersion(dataProduct1Res.getId(), ODMRegistryResources.RESOURCE_DP1_V1);
        verifyBasicContent(descriptorContent);
        DataProductVersionDPDS dataProductVersion = null;
		try {
			dataProductVersion = ObjectMapperFactory.JSON_MAPPER.readValue(descriptorContent, DataProductVersionDPDS.class);
        } catch (Throwable e) {
			fail("Impossible to parse descriptor content");
		} 
        verifyParsedContent(dataProductVersion);

        
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionsCreation() throws IOException {

        DataProductResource dataProduct1Res = createDataProduct(ODMRegistryResources.DP1);

        String descriptor;
        
        descriptor = createDataProductVersion(dataProduct1Res.getId(), ODMRegistryResources.RESOURCE_DP1_V1);
        // TODO verifyBasicContent(descriptor);

        // TODO test also resubmitting the response of previous creation (need a fix on server)
        descriptor = resourceBuilder.readResourceFromFile(ODMRegistryResources.RESOURCE_DP1_V1);
        
        // modify version & re-post
        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        JsonNode rootEntity = null;
        try {
            rootEntity = (ObjectNode) mapper.readTree(descriptor);
            ObjectNode infoNode = (ObjectNode) rootEntity.get("info");
            infoNode.put("version", "1.5.5");
            descriptor = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootEntity);
        } catch (JsonProcessingException e) {
            fail("Impossible to parse response");
        }

        ResponseEntity<String> postProductVersion2Response = registryClient.postDataProductVersion(
                dataProduct1Res.getId(), descriptor, String.class);
        verifyResponseEntity(postProductVersion2Response, HttpStatus.CREATED, true);
        // TODO verifyBasicContent(descriptor);
    }

    // ----------------------------------------
    // READ Data product version
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionsReadAll() throws IOException {

        // create a product and associate to it a version
        DataProductResource dataProduct1Res = createDataProduct(ODMRegistryResources.DP1);
        String descriptorContent = createDataProductVersion(dataProduct1Res.getId(), ODMRegistryResources.RESOURCE_DP1_V1);
        JsonNode descriptorRootNode = verifyJsonSynatx(descriptorContent);
        String versionNumber = descriptorRootNode.get("info").get("version").asText();

        // read all version associated to the created data product
        ResponseEntity<String[]> getDataProductVersionsResponse = registryClient.getDataProductVersions(
                dataProduct1Res.getId(), String[].class);
        verifyResponseEntity(getDataProductVersionsResponse, HttpStatus.OK, true);

        // test response
        String[] dataProductVersionNumbers = getDataProductVersionsResponse.getBody();
        if(dataProductVersionNumbers != null) {
            assertThat(dataProductVersionNumbers.length).isEqualTo(1);
            assertThat(dataProductVersionNumbers[0]).isEqualTo(versionNumber);
        } else {
            fail("Response is empty");
        } 
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    @EnabledIf(expression = "#{environment.acceptsProfiles('testpostgresql', 'dev')}", loadContext = true)
    public void testDataProductVersionsReadOne() throws IOException {

        // create a product and associate to it a version
        DataProductResource dataProduct1Res = createDataProduct(ODMRegistryResources.DP1);
        String descriptorContent = createDataProductVersion(dataProduct1Res.getId(), ODMRegistryResources.RESOURCE_DP1_V1);
        JsonNode descriptorRootNode = verifyJsonSynatx(descriptorContent);
        String versionNumber = descriptorRootNode.get("info").get("version").asText();
        System.out.println(descriptorContent);

        // read the specific version just created
        ResponseEntity<String> getDataProductVersionResponse = registryClient.getDataProductVersion(
                dataProduct1Res.getId(),
                versionNumber, 
                String.class);

        // test response
        verifyResponseEntity(getDataProductVersionResponse, HttpStatus.OK, true);
        verifyBasicContent(getDataProductVersionResponse.getBody());
        DataProductVersionDPDS dataProductVersion = null;
		try {
			dataProductVersion = ObjectMapperFactory.JSON_MAPPER.readValue(getDataProductVersionResponse.getBody(), DataProductVersionDPDS.class);
        } catch (Throwable e) {
			fail("Impossible to parse descriptor content");
		} 
        verifyParsedContent(dataProductVersion);

       
        dataProductVersion = registryClient.readOneDataProductVersion(dataProduct1Res.getId(), versionNumber);
        verifyParsedContent(dataProductVersion);
        
    }

    // ----------------------------------------
    // UPDATE Data product version
    // ----------------------------------------

    // Data product versions are immutable

    // ----------------------------------------
    // Delete Data product version
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionDelete()
            throws IOException {

        // create a product and associate to it a version
        DataProductResource dataProduct1Res = createDataProduct(ODMRegistryResources.DP1);
        String descriptorContent = createDataProductVersion(dataProduct1Res.getId(), ODMRegistryResources.RESOURCE_DP1_V1);
        JsonNode descriptorRootNode = verifyJsonSynatx(descriptorContent);
        String versionNumber = descriptorRootNode.get("info").get("version").asText();

        ResponseEntity<String> deleteVersionResponse = registryClient.deleteDataProductVersion(
                dataProduct1Res.getId(),
                versionNumber, String.class);
        verifyResponseEntity(deleteVersionResponse, HttpStatus.OK, false);

        ResponseEntity<String> getOneDataProductVersionResponse = registryClient.getDataProductVersion(
                dataProduct1Res.getId(),
                versionNumber, String.class);
        verifyResponseEntity(getOneDataProductVersionResponse, HttpStatus.NOT_FOUND, false);

        ResponseEntity<String[]> getAllDataProductVersionsResponse = registryClient.getDataProductVersions(
                dataProduct1Res.getId(), String[].class);
        verifyResponseEntity(getAllDataProductVersionsResponse, HttpStatus.OK, true);

        String[] dataProductVersionNumbers = getAllDataProductVersionsResponse.getBody();
        assertThat(dataProductVersionNumbers.length).isEqualTo(0);
    }

    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Data product version
    // ---------------------------------------- }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductCreation400Errors() throws IOException {

        DataProductResource dataProduct1Res = createDataProduct(ODMRegistryResources.DP1);

        // Test error SC400_01_DESCRIPTOR_IS_EMPTY
        // TODO test also with a null payload (requires a fix on server)
        String payload = " ";
        ResponseEntity<ErrorRes> errorResponse = registryClient.postDataProductVersion(dataProduct1Res.getId(), payload, ErrorRes.class);
        verifyResponseError(errorResponse, HttpStatus.BAD_REQUEST, RegistryApiStandardErrors.SC400_01_DESCRIPTOR_IS_EMPTY);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductCreation422Errors() throws IOException {

        ResponseEntity<ErrorRes> errorResponse;
       
        // create the product
        DataProductResource dataProduct1Res = createDataProduct(ODMRegistryResources.DP1);

        // Test error SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID

        // TODO this should work (fix on server). fqn is optional if name and version are set
        // remove from descriptor fullyQualifiedName property
        String descriptorContent = resourceBuilder.readResourceFromFile(ODMRegistryResources.RESOURCE_DP1_V1);
        JsonNode descriptorRootEntity = mapper.readTree(descriptorContent);
        ObjectNode infoObject = (ObjectNode) descriptorRootEntity.get("info");
        infoObject.remove("fullyQualifiedName");
        descriptorContent = mapper.writeValueAsString(descriptorRootEntity);

        errorResponse = registryClient.postDataProductVersion(dataProduct1Res.getId(), descriptorContent, ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID);

        // Test error SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID
        errorResponse = registryClient.postDataProductVersion(dataProduct1Res.getId(), 
            "This is an invalid JSON document", ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_02_DESCRIPTOR_DOC_SYNTAX_IS_INVALID);

        // Test error SC422_05_VERSION_ALREADY_EXISTS

        // associate the version to the product
        createDataProductVersion(dataProduct1Res.getId(), ODMRegistryResources.RESOURCE_DP1_V1);
        // and the re associate it
        descriptorContent = resourceBuilder.readResourceFromFile(ODMRegistryResources.RESOURCE_DP1_V1);
        errorResponse = registryClient.postDataProductVersion(dataProduct1Res.getId(), 
            descriptorContent, ErrorRes.class);
        verifyResponseError(errorResponse,
                HttpStatus.UNPROCESSABLE_ENTITY,
                RegistryApiStandardErrors.SC422_05_VERSION_ALREADY_EXISTS);
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

    private DataProductVersionDPDS verifyParsedContent(DataProductVersionDPDS dpVersion) {
        assertThat(dpVersion.getInternalComponents()).isNotNull();
        assertThat(dpVersion.getInternalComponents().getLifecycleInfo()).isNotNull();
        assertThat(dpVersion.getInternalComponents().getLifecycleInfo().getActivityInfos()).isNotNull();
        assertThat(dpVersion.getInternalComponents().getLifecycleInfo().getActivityInfos().size()).isEqualTo(2);
    
        return dpVersion;
    }

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
            assertThat(apiDefinitionObject.get("$ref").asText())
                    .matches(Pattern.compile("http://localhost:\\d*/api/v1/pp/registry/apis/\\d*"));
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
       

        // test lifecycleInfo
        JsonNode lifecycleInfoObject = internalComponentsObject.path("lifecycleInfo");
        assertThat(lifecycleInfoObject).isNotNull();
        ObjectNode stage = null, template = null;
        stage = (ObjectNode)lifecycleInfoObject.get("dev");
        assertThat(stage).isNotNull();
        template = (ObjectNode)stage.get("template");
        assertThat(template).isNotNull();
        assertThat(template.get("definition")).isNotNull();
        assertThat(template.get("definition").get("$ref")).isNotNull();
        assertThat(template.get("definition").get("$ref").asText())
                    .matches(Pattern.compile("http://localhost:\\d*/api/v1/pp/registry/templates/\\d*"));
        
        
        stage = (ObjectNode)lifecycleInfoObject.get("prod");
        assertThat(stage).isNotNull();
        template = (ObjectNode)stage.get("template");
        assertThat(template).isNotNull();

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

        // test infra components
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