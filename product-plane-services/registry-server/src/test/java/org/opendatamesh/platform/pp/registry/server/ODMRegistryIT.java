package org.opendatamesh.platform.pp.registry.server;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opendatamesh.platform.core.commons.test.ODMIntegrationTest;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryClient;
import org.opendatamesh.platform.pp.registry.api.resources.*;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryResourceBuilder;
import org.opendatamesh.platform.pp.registry.server.utils.ODMRegistryTestResources;
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
//@ActiveProfiles("test")
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

    protected String getResourceContent(ODMRegistryTestResources resource) {
        String result = null;
        try {
            result = resource.getContent();
        } catch (IOException t) {
            t.printStackTrace();
            fail("Impossible to read data product version from file: " + t.getMessage());
        }
        return result;
    }

    protected <T> T getResourceObject(ODMRegistryTestResources resource, Class<T> resourceType) {
        T result = null;
        
        try {
            result = resource.getObject(resourceType);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read resource from file: " + t.getMessage());
        }

        return result;
    }

    protected DataProductResource createDataProduct(ODMRegistryTestResources resource) {

        DataProductResource createdActivityRes = null;

        DataProductResource activityRes;
        try {
            activityRes = resource.getObject(DataProductResource.class);
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
    protected String createDataProductVersion(String dataProductId, ODMRegistryTestResources resource) {
        String descriptorContent = getResourceContent(resource);
        return createDataProductVersion(dataProductId, descriptorContent);
    }

    protected String uploadDataProductVersion(DataProductDescriptorLocationResource descriptorLocation) {
       
        ResponseEntity<String> uploadProductVersionResponse = null;
       
        try {
            uploadProductVersionResponse = registryClient.uploadDataProductVersion(descriptorLocation, String.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to upload data product version: " + t.getMessage());
            return null;
        }
        
        verifyResponseEntity(uploadProductVersionResponse, HttpStatus.CREATED, true);

        return uploadProductVersionResponse.getBody();
    }


  
    protected ExternalComponentResource createApi(ODMRegistryTestResources resource) {
        ExternalComponentResource apiDefinitionRes = getResourceObject(resource, ExternalComponentResource.class);
        return createApi(apiDefinitionRes);
    }

    protected ExternalComponentResource createApi(ExternalComponentResource apiRes) {
        ExternalComponentResource createdApiRes = null;

        ResponseEntity<ExternalComponentResource> response = null;
        
        try {
            response = registryClient.postApi(apiRes);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create api definition: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(response, HttpStatus.CREATED, true);
        createdApiRes = response.getBody();

        return createdApiRes;
    }

    protected ExternalComponentResource createTemplate(ODMRegistryTestResources resource) {
        ExternalComponentResource templateRes = getResourceObject(resource, ExternalComponentResource.class);
        return createTemplate(templateRes);
    }

    protected ExternalComponentResource createTemplate(ExternalComponentResource templateRes) {
        ExternalComponentResource createdTemplateRes = null;

        ResponseEntity<ExternalComponentResource> response = null;
        
        try {
            response = registryClient.postTemplate(templateRes);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create template definition: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(response, HttpStatus.CREATED, true);
        createdTemplateRes = response.getBody();

        return createdTemplateRes;
    }
  
  

    protected SchemaResource createSchema1() throws IOException {
        SchemaResource schemaResource = ODMRegistryTestResources.RESOURCE_SCHEMA1.getObject(SchemaResource.class);
        ResponseEntity<SchemaResource> postSchemaResponse = registryClient.postSchema(schemaResource);
        verifyResponseEntity(postSchemaResponse, HttpStatus.CREATED, true);

        return postSchemaResponse.getBody();

    }

    protected DomainResource createDomain1() throws IOException {
        DomainResource payload = ODMRegistryTestResources.RESOURCE_DOMAIN1.getObject(DomainResource.class);
        ResponseEntity<DomainResource> postDomainResponse = registryClient.createDomain(payload);
        verifyResponseEntity(postDomainResponse, HttpStatus.CREATED, true);
        return postDomainResponse.getBody();
    }

    protected OwnerResource createOwner1() throws IOException {
        OwnerResource ownerResource = resourceBuilder.buildOwner("test@test.it", "IT Department");
        ResponseEntity<OwnerResource> ownerResponse = registryClient.createOwner(ownerResource);
        verifyResponseEntity(ownerResponse, HttpStatus.CREATED, true);
        return ownerResponse.getBody();
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
