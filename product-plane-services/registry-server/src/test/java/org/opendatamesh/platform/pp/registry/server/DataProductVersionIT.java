package org.opendatamesh.platform.pp.registry.server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.Test;
import org.opendatamesh.dpds.exceptions.ParseException;
import org.opendatamesh.dpds.location.DescriptorLocation;
import org.opendatamesh.dpds.location.UriLocation;
import org.opendatamesh.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.dpds.model.internals.ApplicationComponentDPDS;
import org.opendatamesh.dpds.model.internals.InfrastructuralComponentDPDS;
import org.opendatamesh.dpds.parser.DPDSParser;
import org.opendatamesh.dpds.parser.IdentifierStrategyFactory;
import org.opendatamesh.dpds.parser.ParseOptions;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryTestResources;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class DataProductVersionIT extends ODMRegistryIT {

    // ======================================================================================
    // CREATE Data Product Version
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDPVersion() {

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildTestDataProduct();
        createdDataProductRes = createDataProduct(createdDataProductRes);

        String descriptorContent = createDataProductVersion(createdDataProductRes.getId(), ODMRegistryTestResources.DPD_CORE);
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(descriptorContent);

        DataProductVersionDPDS dataProductVersion = null;
        
        DPDSParser parser = new DPDSParser(
                "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/schemas/",
                "1.0.0",
                null
        );
        DescriptorLocation location = new UriLocation(descriptorContent);
        ParseOptions options = new ParseOptions();
        options.setIdentifierStrategy(IdentifierStrategyFactory.getDefault());
        options.setRewriteEntityType(false);
        options.setServerUrl("http://localhost");
        try {
            dataProductVersion = parser.parse(location, options).getDescriptorDocument();
        } catch (ParseException e) {
            fail("Impossible to parse descriptor content");
        }
        ODMRegistryTestResources.DPD_CORE.getObjectChecker().verifyAll(dataProductVersion);
        
        try {
            ObjectMapper mapper = ObjectMapperFactory.getRightMapper(descriptorContent);
            dataProductVersion = mapper.readValue(descriptorContent,
                    DataProductVersionDPDS.class);
        } catch (Throwable e) {
            fail("Impossible to parse descriptor content");
        }
        ODMRegistryTestResources.DPD_CORE.getObjectChecker().verifyAll(dataProductVersion);
    }

    private String changeDescriptorVersion(String descriptorContent, String newVersion) {
        // Change version number
        String newVersionDescriptorContent = null;
        try {
            JsonNode descriptorRootEntity = mapper.readTree(descriptorContent);
            ObjectNode infoObject = (ObjectNode) descriptorRootEntity.get("info");
            infoObject.put("version", newVersion);
            newVersionDescriptorContent = mapper.writeValueAsString(descriptorRootEntity);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to modify data product version 1.0.0: " + t.getMessage());
        }
        return newVersionDescriptorContent;
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateMultipleDPVersions() {

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildTestDataProduct();
        createdDataProductRes = createDataProduct(createdDataProductRes);

        String descriptorContent = getResourceContent(ODMRegistryTestResources.DPD_CORE);
        String createdDescriptorContent = createDataProductVersion(createdDataProductRes.getId(), descriptorContent);
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(createdDescriptorContent);

        // Change version number
        String newVersionDescriptorContent = changeDescriptorVersion(descriptorContent, "2.0.0");
        String newCreatedVersionDescriptorContent = createDataProductVersion(createdDataProductRes.getId(), newVersionDescriptorContent);

        
        String[] versions = null;
        try {
            versions = registryClient.readAllDataProductVersions(createdDataProductRes.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read versions of data product [" + createdDataProductRes.getId() + "]: " + t.getMessage());
        }
        assertThat(versions).isNotNull();
        assertThat(versions.length).isEqualTo(2);

        ExternalComponentResource[] apis = null;
        try {
            apis =  registryClient.readAllApis();
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read apis of data product [" + createdDataProductRes.getId() + "]: " + t.getMessage());
        }
        assertThat(apis).isNotNull();
        assertThat(apis.length).isEqualTo(2);

        ExternalComponentResource[] templates = null;
        try {
            templates =  registryClient.getTemplates().getBody();
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read templates of data product [" + createdDataProductRes.getId() + "]: " + t.getMessage());
        }
        assertThat(templates).isNotNull();
        assertThat(templates.length).isEqualTo(1);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateDPVersionFromPreviousVersion() {

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildTestDataProduct();
        createdDataProductRes = createDataProduct(createdDataProductRes);


        String createdDescriptorContent = createDataProductVersion(createdDataProductRes.getId(), ODMRegistryTestResources.DPD_CORE);
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(createdDescriptorContent);

        // Change version number
        String newVersionDescriptorContent = changeDescriptorVersion(createdDescriptorContent, "2.0.0");
        String newCreatedVersionDescriptorContent = createDataProductVersion(createdDataProductRes.getId(), newVersionDescriptorContent);
        
        
        assertThat(newCreatedVersionDescriptorContent).isNotNull();

        String[] versions = null;
        try {
            versions = registryClient.readAllDataProductVersions(createdDataProductRes.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read versions of data product [" + createdDataProductRes.getId() + "]: " + t.getMessage());
        }
        assertThat(versions).isNotNull();
        assertThat(versions.length).isEqualTo(2);

        ExternalComponentResource[] apis = null;
        try {
            apis =  registryClient.readAllApis();
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read apis of data product [" + createdDataProductRes.getId() + "]: " + t.getMessage());
        }
        assertThat(apis).isNotNull();
        assertThat(apis.length).isEqualTo(2);

        ExternalComponentResource[] templates = null;
        try {
            templates =  registryClient.getTemplates().getBody();
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read templates of data product [" + createdDataProductRes.getId() + "]: " + t.getMessage());
        }
        assertThat(templates).isNotNull();
        assertThat(templates.length).isEqualTo(1);
    }

    // ======================================================================================
    // READ Data Product Versions
    // ======================================================================================
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionsReadAll() throws IOException {

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildTestDataProduct();
        createdDataProductRes = createDataProduct(createdDataProductRes);

        String descriptorContent = getResourceContent(ODMRegistryTestResources.DPD_CORE);
        String createdDescriptorContent = createDataProductVersion(createdDataProductRes.getId(), descriptorContent);
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(createdDescriptorContent);

        // Change version number
        String newVersionDescriptorContent = changeDescriptorVersion(descriptorContent, "2.0.0");
        String newCreatedVersionDescriptorContent = createDataProductVersion(createdDataProductRes.getId(), newVersionDescriptorContent);


        // read all version associated to the created data product
        ResponseEntity<String[]> getDataProductVersionsResponse = registryClient.getDataProductVersions(
                createdDataProductRes.getId(), String[].class);
        verifyResponseEntity(getDataProductVersionsResponse, HttpStatus.OK, true);

        // test response
        String[] dataProductVersionNumbers = getDataProductVersionsResponse.getBody();
        assertThat(dataProductVersionNumbers).isNotNull();
        assertThat(dataProductVersionNumbers.length).isEqualTo(2);
        assertThat(dataProductVersionNumbers).containsExactly("1.0.0", "2.0.0");
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testDataProductVersionsReadOne() {

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildTestDataProduct();
        createdDataProductRes = createDataProduct(createdDataProductRes);

        String descriptorContent = getResourceContent(ODMRegistryTestResources.DPD_CORE);
        String createdDescriptorContent = createDataProductVersion(createdDataProductRes.getId(), descriptorContent);
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(createdDescriptorContent);

        // Change version number
        String newVersionDescriptorContent = changeDescriptorVersion(descriptorContent, "2.0.0");
        String newCreatedVersionDescriptorContent = createDataProductVersion(createdDataProductRes.getId(), newVersionDescriptorContent);


        // read the specific version just created
        ResponseEntity<String> getDataProductVersionResponse = registryClient.getDataProductVersion(
                createdDataProductRes.getId(),
                "1.0.0",
                String.class);

        // test response
        verifyResponseEntity(getDataProductVersionResponse, HttpStatus.OK, true);
        
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(createdDescriptorContent);

    }

    // ======================================================================================
    // READ Data Product Version Components
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testGetDataProductVersionApplicationComponents() throws JsonProcessingException {

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildDataProduct(
                "f350cab5-992b-32f7-9c90-79bca1bf10be",
                "urn:org.opendatamesh:dataproducts:dpdCore",
                "Test Domain",
                "This is test product #1");
        createdDataProductRes = createDataProduct(createdDataProductRes);

        String descriptorContent = createDataProductVersion(createdDataProductRes.getId(), ODMRegistryTestResources.DPD_CORE);

        ResponseEntity<ApplicationComponentDPDS[]> applicationComponentDPDSResponse = registryClient.getDataProductVersionApplicationComponents(createdDataProductRes.getId(), "1.0.0");
        verifyResponseEntity(applicationComponentDPDSResponse, HttpStatus.OK, true);
        assertThat(applicationComponentDPDSResponse.getBody().length).isEqualTo(1);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testGetDataProductVersionInfrastructuralComponents() throws JsonProcessingException {

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildDataProduct(
                "f350cab5-992b-32f7-9c90-79bca1bf10be",
                "urn:org.opendatamesh:dataproducts:dpdCore",
                "Test Domain",
                "This is test product #1");
        createdDataProductRes = createDataProduct(createdDataProductRes);

        String descriptorContent = createDataProductVersion(createdDataProductRes.getId(), ODMRegistryTestResources.DPD_CORE);

        ResponseEntity<InfrastructuralComponentDPDS[]> infrastructuralComponentDPDSResponse = registryClient.getDataProductVersionInfrastructuralComponents(createdDataProductRes.getId(), "1.0.0");
        verifyResponseEntity(infrastructuralComponentDPDSResponse, HttpStatus.OK, true);
        assertThat(infrastructuralComponentDPDSResponse.getBody().length).isEqualTo(1);

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

        DataProductResource createdDataProductRes = null;
        createdDataProductRes = resourceBuilder.buildTestDataProduct();
        createdDataProductRes = createDataProduct(createdDataProductRes);

        String descriptorContent = getResourceContent(ODMRegistryTestResources.DPD_CORE);
        String createdDescriptorContent = createDataProductVersion(createdDataProductRes.getId(), descriptorContent);
        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(createdDescriptorContent);

        // Change version number
        String newVersionDescriptorContent = changeDescriptorVersion(descriptorContent, "2.0.0");
        String newCreatedVersionDescriptorContent = createDataProductVersion(createdDataProductRes.getId(), newVersionDescriptorContent);

        ResponseEntity<String[]> getAllDataProductVersionsResponse = registryClient.getDataProductVersions(
                createdDataProductRes.getId(), String[].class);
        verifyResponseEntity(getAllDataProductVersionsResponse, HttpStatus.OK, true);
        String[] dataProductVersionNumbers = getAllDataProductVersionsResponse.getBody();
        assertThat(dataProductVersionNumbers.length).isEqualTo(2);

        // DELETE
        ResponseEntity<String> deleteVersionResponse = registryClient.deleteDataProductVersion(
                createdDataProductRes.getId(),
                "2.0.0", String.class);
        verifyResponseEntity(deleteVersionResponse, HttpStatus.OK, false);

        ResponseEntity<String> getOneDataProductVersionResponse = registryClient.getDataProductVersion(
                createdDataProductRes.getId(),
                "2.0.0", String.class);
        verifyResponseEntity(getOneDataProductVersionResponse, HttpStatus.NOT_FOUND, false);

        getAllDataProductVersionsResponse = registryClient.getDataProductVersions(
                createdDataProductRes.getId(), String[].class);
        verifyResponseEntity(getAllDataProductVersionsResponse, HttpStatus.OK, true);
        dataProductVersionNumbers = getAllDataProductVersionsResponse.getBody();
        assertThat(dataProductVersionNumbers.length).isEqualTo(1);

        ResponseEntity<String> getDataProductVersionsResponse = registryClient.getDataProductVersion(
                createdDataProductRes.getId(), "1.0.0", String.class);
        verifyResponseEntity(getAllDataProductVersionsResponse, HttpStatus.OK, true);
        String readDescriptorContent = getDataProductVersionsResponse.getBody();

        ODMRegistryTestResources.DPD_CORE.getContentChecker().verifyAll(createdDescriptorContent);
    }   

    // ======================================================================================
    // PRIVATE METHODS
    // ======================================================================================

    // ----------------------------------------
    // Create test resources
    // ----------------------------------------

    // TODO ...as needed

    
}