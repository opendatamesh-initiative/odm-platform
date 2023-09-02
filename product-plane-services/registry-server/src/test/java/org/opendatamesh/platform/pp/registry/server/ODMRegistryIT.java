package org.opendatamesh.platform.pp.registry.server;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opendatamesh.platform.core.commons.clients.ODMIntegrationTest;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryClient;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductDescriptorLocationResource;
import org.opendatamesh.platform.pp.registry.api.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.resources.DefinitionResource;
import org.opendatamesh.platform.pp.registry.api.resources.RegistryApiStandardErrors;
import org.opendatamesh.platform.pp.registry.api.resources.SchemaResource;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryResourceBuilder;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryResources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.jdbc.JdbcTestUtils;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
//@ActiveProfiles("dev")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { ODMRegistryApp.class })
public abstract class ODMRegistryIT extends ODMIntegrationTest {

    @LocalServerPort
    protected String port;

    protected RegistryClient registryClient;

    protected ODMRegistryResourceBuilder resourceBuilder;
    
    @Autowired
    protected ObjectMapper mapper;

    protected final String DB_TABLES_POSTGRESQL = "src/test/resources/db/tables_postgresql.txt";
    protected final String DB_TABLES_MYSQL = "src/test/resources/db/tables_mysql.txt";


    protected Logger logger = LoggerFactory.getLogger(ODMRegistryIT.class);

   
    @PostConstruct
    public final void init() {
        resourceBuilder = new ODMRegistryResourceBuilder();
        registryClient = new RegistryClient("http://localhost:" + port);
    }


    @BeforeEach
    public void cleanDbState(@Autowired JdbcTemplate jdbcTemplate, @Autowired Environment environment) throws IOException {
        String activeProfile = Arrays.stream(environment.getActiveProfiles()).findFirst().get();
        String[] tableSet;
        if(activeProfile.equals("testpostgresql")) {
            tableSet = Files.readAllLines(new File(DB_TABLES_POSTGRESQL).toPath(), Charset.defaultCharset()).toArray(new String[0]);
            JdbcTestUtils.deleteFromTables(
                    jdbcTemplate,
                    tableSet
            );
        } else if (activeProfile.equals("testmysql") || activeProfile.equals("localmysql")) {
            tableSet = Files.readAllLines(new File(DB_TABLES_MYSQL).toPath(), Charset.defaultCharset()).toArray(new String[0]);
            JdbcTestUtils.deleteFromTables(
                    jdbcTemplate,
                    tableSet
            );
        }
    }

    // ======================================================================================
    // Create test basic resources
    // ======================================================================================

    protected DataProductResource createDataProduct(ODMRegistryResources resource) {

        DataProductResource createdActivityRes = null;

        DataProductResource activityRes;
        try {
            activityRes = resourceBuilder.readResourceFromFile(resource, DataProductResource.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read data product from file: " + t.getMessage());
            return null;
        }
        createdActivityRes = createDataProduct(activityRes);

        return createdActivityRes;
    }

    protected DataProductResource createDataProduct(DataProductResource dataProductRes) {
        DataProductResource createdDataProductRes = null;

        ResponseEntity<DataProductResource> postDataProductResponse = null;
        
        try {
            postDataProductResponse = registryClient.postDataProduct(dataProductRes);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create data product: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(postDataProductResponse, HttpStatus.CREATED, true);
        createdDataProductRes = postDataProductResponse.getBody();

        return createdDataProductRes;
    }

    protected String getDescriptorContent(ODMRegistryResources resource) {
        String descriptorContent = null;
        try {
            descriptorContent = resourceBuilder.getContent(resource);
        } catch (IOException t) {
            t.printStackTrace();
            fail("Impossible to read data product version from file: " + t.getMessage());
        }
        return descriptorContent;
    }

    protected String createDataProductVersion(String dataProductId, String descriptorContent) {
        ResponseEntity<String> postProductVersionResponse = null;
        try {
            postProductVersionResponse = registryClient.postDataProductVersion(dataProductId, descriptorContent, String.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post data product version: " + t.getMessage());
            return null;
        }
        verifyResponseEntity(postProductVersionResponse, HttpStatus.CREATED, true);
        
        return postProductVersionResponse.getBody();
    }
    protected String createDataProductVersion(String dataProductId, ODMRegistryResources resource) {
        String descriptorContent = getDescriptorContent(resource);
        return createDataProductVersion(dataProductId, descriptorContent);
    }

    protected String uploadDataProductVersion(DataProductDescriptorLocationResource descriptorLocation) throws IOException {
        ResponseEntity<String> uploadProductVersionResponse = 
            registryClient.uploadDataProductVersion(descriptorLocation, String.class);
        verifyResponseEntity(uploadProductVersionResponse, HttpStatus.CREATED, true);

        return uploadProductVersionResponse.getBody();
    }

    protected DefinitionResource createApiDefinition(ODMRegistryResources resource) {


        DefinitionResource createdApiDefinitionRes = null;

        DefinitionResource apiDefinitionRes;
        try {
            apiDefinitionRes = resourceBuilder.readResourceFromFile(resource, DefinitionResource.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read api definition from file: " + t.getMessage());
            return null;
        }
        createdApiDefinitionRes = createApiDefinition(apiDefinitionRes);

        return createdApiDefinitionRes;
    }

    protected DefinitionResource createApiDefinition(DefinitionResource dataProductRes) {
        DefinitionResource createdApiDefinitionRes = null;

        ResponseEntity<DefinitionResource> postApiDefinitionResponse = null;
        
        try {
            postApiDefinitionResponse = registryClient.postApiDefinition(dataProductRes);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create api definition: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(postApiDefinitionResponse, HttpStatus.CREATED, true);
        createdApiDefinitionRes = postApiDefinitionResponse.getBody();

        return createdApiDefinitionRes;
    }

    protected DefinitionResource createTemplate(ODMRegistryResources resource) throws IOException {

        String payload = resourceBuilder.getContent(resource);
        ResponseEntity<DefinitionResource> postTemplate = registryClient.postTemplateDefinition(payload);
        verifyResponseEntity(postTemplate, HttpStatus.CREATED, true);
        return postTemplate.getBody();
    }

   
    protected DefinitionResource createTemplate(DefinitionResource templateDefinitionRes) throws IOException {
        ResponseEntity<DefinitionResource> postTemplate = registryClient.postTemplateDefinition(
                templateDefinitionRes
        );
        verifyResponseEntity(postTemplate, HttpStatus.CREATED, true);
        return postTemplate.getBody();
    }




    protected SchemaResource createSchema1() throws IOException {
        SchemaResource schemaResource = resourceBuilder.readResourceFromFile(ODMRegistryResources.RESOURCE_SCHEMA1, SchemaResource.class);
        ResponseEntity<SchemaResource> postSchemaResponse = registryClient.postSchema(schemaResource);
        verifyResponseEntity(postSchemaResponse, HttpStatus.CREATED, true);

        return postSchemaResponse.getBody();

    }

    // ======================================================================================
    // Verify test basic resources
    // ======================================================================================

    protected JsonNode verifyJsonSynatx(String responseBody) {
        ObjectMapper mapper = ObjectMapperFactory.JSON_MAPPER;
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
            RegistryApiStandardErrors error) {
        assertThat(errorResponse.getStatusCode())
                .isEqualByComparingTo(status);
        assertThat(errorResponse.getBody().getCode())
                .isEqualTo(error.code());
        assertThat(errorResponse.getBody().getDescription())
                .isEqualTo(error.description());
    }

    // ======================================================================================
    // Verify response
    // ======================================================================================

    // TODO ...add as needed

}
