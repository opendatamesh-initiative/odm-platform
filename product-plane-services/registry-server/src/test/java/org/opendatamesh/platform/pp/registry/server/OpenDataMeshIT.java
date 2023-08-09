package org.opendatamesh.platform.pp.registry.server;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.registry.api.v1.clients.RegistryClient;
import org.opendatamesh.platform.pp.registry.api.v1.exceptions.ODMRegistryAPIStandardError;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DataProductDescriptorLocationResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DataProductResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DefinitionResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.v1.resources.SchemaResource;
import org.opendatamesh.platform.pp.registry.server.ODMRegistryApp;
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

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(SpringExtension.class)
//@ActiveProfiles("dev")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { ODMRegistryApp.class })
public abstract class OpenDataMeshIT {

    @LocalServerPort
    protected String port;

    protected RegistryClient registryClient;

    protected ResourceBuilder resourceBuilder;
    
    @Autowired
    protected ObjectMapper mapper;

    protected Logger logger = LoggerFactory.getLogger(OpenDataMeshIT.class);

    protected final String DB_TABLES_POSTGRESQL = "src/test/resources/db/tables_postgresql.txt";
    protected final String DB_TABLES_MYSQL = "src/test/resources/db/tables_mysql.txt";
    protected final String RESOURCE_DP1 = "src/test/resources/test/dataproduct-descriptor/dp1.json";
    protected final String RESOURCE_DP1_UPD = "src/test/resources/test/dataproduct-descriptor/dp1-updated.json";
    protected final String RESOURCE_DP1_V1 = "src/test/resources/test/dataproduct-descriptor/dp1-v1.json";
    
    //protected final String RESOURCE_DP1_V1_API1 = "src/test/resources/test/dataproduct-descriptor/dp1-v1-api1.json";
    protected final String RESOURCE_DEF1_V1 = "src/test/resources/test/definition/def1.json";
    protected final String RESOURCE_DEF1_NOVERSION = "src/test/resources/test/definition/def1-missing-version.json";
    protected final String RESOURCE_DEF1_NONAME = "src/test/resources/test/definition/def1-missing-name.json";
    protected final String RESOURCE_DEF1_NONAME_NOVERSION = "src/test/resources/test/definition/def2-missing-name-version.json";
    
    protected final String RESOURCE_TEMPLATE_1 = "src/test/resources/test/template/template1.json";
    protected final String RESOURCE_TEMPLATE_2 = "src/test/resources/test/template/template2.json";
    protected final String RESOURCE_SCHEMA1 = "src/test/resources/test/schema/schema1.json";
    protected final String RESOURCE_DPS_URI = "https://raw.githubusercontent.com/opendatamesh-initiative/odm-specification-dpdescriptor/main/examples/tripexecution/data-product-descriptor.json";
        
    @PostConstruct
    public final void init() {
        resourceBuilder = new ResourceBuilder();
        registryClient = new RegistryClient("http://localhost:" + port);
    }

    @Before
    public void setup() {
        // objectMapper = DataProductDescriptor.buildObjectMapper();
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

    protected DataProductResource createDataProduct(String filePath) throws IOException {
        String payload = resourceBuilder.readResourceFromFile(filePath);
        ResponseEntity<DataProductResource> postProductResponse = registryClient.postDataProduct(payload);
        verifyResponseEntity(postProductResponse, HttpStatus.CREATED, true);
        return postProductResponse.getBody();
    }

    protected DataProductResource updateDataProduct(String filePath) throws IOException {
        String payload = resourceBuilder.readResourceFromFile(filePath);
        ResponseEntity<DataProductResource> putProductResponse = registryClient.putDataProduct(payload);
        verifyResponseEntity(putProductResponse, HttpStatus.OK, true);
        return putProductResponse.getBody();
    }

    protected String createDataProductVersion(String dataProductId, String filePath) throws IOException {
        String payload = resourceBuilder.readResourceFromFile(filePath);
        ResponseEntity<String> postProductVersionResponse = registryClient.postDataProductVersion(
                dataProductId, payload, String.class);
        verifyResponseEntity(postProductVersionResponse, HttpStatus.CREATED, true);
        return postProductVersionResponse.getBody();
    }

    protected String uploadDataProductVersion(DataProductDescriptorLocationResource descriptorLocation) throws IOException {
        ResponseEntity<String> uploadProductVersionResponse = 
            registryClient.uploadDataProductVersion(descriptorLocation, String.class);
        verifyResponseEntity(uploadProductVersionResponse, HttpStatus.CREATED, true);

        return uploadProductVersionResponse.getBody();
    }


    protected DefinitionResource createApiDefinition(String filePath) throws IOException {
        String payload = resourceBuilder.readResourceFromFile(filePath);
        ResponseEntity<DefinitionResource> postDefinition = registryClient.postApiDefinition(payload);
        verifyResponseEntity(postDefinition, HttpStatus.CREATED, true);
        return postDefinition.getBody();
    }

    protected DefinitionResource createTemplate(String filePath) throws IOException {
        String payload = resourceBuilder.readResourceFromFile(filePath);
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
        SchemaResource schemaResource = resourceBuilder.readResourceFromFile(RESOURCE_SCHEMA1, SchemaResource.class);
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
            ODMRegistryAPIStandardError error) {
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
