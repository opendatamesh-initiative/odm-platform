package org.opendatamesh.platform.pp.registry.server;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryAPIRoutes;
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

    // ======================================================================================
    // CREATE Data Product Version
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDPVersion() {

        DataProductResource dataProduct1Res = createDataProduct(ODMRegistryResources.DP1);
       
        String descriptorContent = createDataProductVersion(dataProduct1Res.getId(), ODMRegistryResources.RESOURCE_DP1_V1);
        verifyBasicContent(descriptorContent);
        
        DataProductVersionDPDS dataProductVersion = null;
        try {
            dataProductVersion = ObjectMapperFactory.JSON_MAPPER.readValue(descriptorContent,
                    DataProductVersionDPDS.class);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor content");
        }
        verifyParsedContent(dataProductVersion);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDPVersionWithMissingFqn() {

        DataProductResource dataProductRes = createDataProduct(ODMRegistryResources.DP1);

        String descriptorContent = null;
        try {
            descriptorContent = resourceBuilder.readResourceFromFile(ODMRegistryResources.RESOURCE_DP1_V1);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data product version from file: " + t.getMessage());
        }

        //remove from descriptor fullyQualifiedName property
        try {
            JsonNode descriptorRootEntity = mapper.readTree(descriptorContent);
            ObjectNode infoObject = (ObjectNode) descriptorRootEntity.get("info");
            infoObject.remove("fullyQualifiedName");
            descriptorContent = mapper.writeValueAsString(descriptorRootEntity);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to modify data product version: " + t.getMessage());
        }
        
        try {
            descriptorContent = registryClient.postDataProductVersion(dataProductRes.getId(), descriptorContent, String.class).getBody();
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post data product version: " + t.getMessage());
        }

        verifyBasicContent(descriptorContent);
        
        DataProductVersionDPDS dataProductVersion = null;
        try {
            dataProductVersion = ObjectMapperFactory.JSON_MAPPER.readValue(descriptorContent,
                    DataProductVersionDPDS.class);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor content");
        }
        verifyParsedContent(dataProductVersion);

       
    }

    @Test
    @Disabled
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDPVersionFromPreviousVersion() {

        DataProductResource dataProduct1Res = createDataProduct(ODMRegistryResources.DP1);
       
        String createdDescriptorContent = createDataProductVersion(dataProduct1Res.getId(), ODMRegistryResources.RESOURCE_DP1_V1);
        verifyBasicContent(createdDescriptorContent);

        //remove from descriptor fullyQualifiedName property
        String newVersionDescriptorContent = null;
        try {
            JsonNode descriptorRootEntity = mapper.readTree(createdDescriptorContent);
            ObjectNode infoObject = (ObjectNode) descriptorRootEntity.get("info");
            infoObject.put("version", "2.0.0");
            newVersionDescriptorContent = mapper.writeValueAsString(descriptorRootEntity);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to modify data product version 1.0.0: " + t.getMessage());
        }

        ResponseEntity<String> response = null;
        try {
            response = registryClient.postDataProductVersion(dataProduct1Res.getId(), newVersionDescriptorContent, String.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post data product version 2.0.0: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();

        String newCreatedVersionDescriptorContent = response.getBody();
        assertThat(newCreatedVersionDescriptorContent).isNotNull();
        verifyBasicContent(newCreatedVersionDescriptorContent);
        
    }



    // ======================================================================================
    // CREATE Data Product Version
    // ======================================================================================
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionsReadAll() throws IOException {

        // create a product and associate to it a version
        DataProductResource dataProduct1Res = createDataProduct(ODMRegistryResources.DP1);
        String descriptorContent = createDataProductVersion(dataProduct1Res.getId(),
                ODMRegistryResources.RESOURCE_DP1_V1);
        JsonNode descriptorRootNode = verifyJsonSynatx(descriptorContent);
        String versionNumber = descriptorRootNode.get("info").get("version").asText();

        // read all version associated to the created data product
        ResponseEntity<String[]> getDataProductVersionsResponse = registryClient.getDataProductVersions(
                dataProduct1Res.getId(), String[].class);
        verifyResponseEntity(getDataProductVersionsResponse, HttpStatus.OK, true);

        // test response
        String[] dataProductVersionNumbers = getDataProductVersionsResponse.getBody();
        if (dataProductVersionNumbers != null) {
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
        String descriptorContent = createDataProductVersion(dataProduct1Res.getId(),
                ODMRegistryResources.RESOURCE_DP1_V1);
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
            dataProductVersion = ObjectMapperFactory.JSON_MAPPER.readValue(
                    getDataProductVersionResponse.getBody(), DataProductVersionDPDS.class);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor content");
        }
        verifyParsedContent(dataProductVersion);

        dataProductVersion = registryClient.readOneDataProductVersion(dataProduct1Res.getId(), versionNumber);
        verifyParsedContent(dataProductVersion);

    }

    // ======================================================================================
    // UPDATE Data Product Version
    // ======================================================================================

    // Data product versions are immutable

    // ======================================================================================
    // DELETE Data Product Version
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionDelete()
            throws IOException {

        // create a product and associate to it a version
        DataProductResource dataProduct1Res = createDataProduct(ODMRegistryResources.DP1);
        String descriptorContent = createDataProductVersion(dataProduct1Res.getId(),
                ODMRegistryResources.RESOURCE_DP1_V1);
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
        stage = (ObjectNode) lifecycleInfoObject.get("dev");
        assertThat(stage).isNotNull();
        template = (ObjectNode) stage.get("template");
        assertThat(template).isNotNull();
        assertThat(template.get("definition")).isNotNull();
        assertThat(template.get("definition").get("$ref")).isNotNull();
        assertThat(template.get("definition").get("$ref").asText())
                .matches(Pattern.compile("http://localhost:\\d*/api/v1/pp/registry/templates/\\d*"));

        stage = (ObjectNode) lifecycleInfoObject.get("prod");
        assertThat(stage).isNotNull();
        template = (ObjectNode) stage.get("template");
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