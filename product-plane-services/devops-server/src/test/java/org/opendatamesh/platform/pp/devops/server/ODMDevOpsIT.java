package org.opendatamesh.platform.pp.devops.server;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.io.IOException;
import java.util.Arrays;

import javax.annotation.PostConstruct;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsClient;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.registry.api.v1.resources.ErrorRes;
import org.opendatamesh.platform.pp.registry.api.v1.resources.OpenDataMeshAPIStandardError;
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
@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { ODMDevOpsApp.class })
public abstract class ODMDevOpsIT {

    @LocalServerPort
    protected String port;

    protected DevOpsClient devOpsClient;
    protected ResourceBuilder resourceBuilder;
    
    @Autowired
    protected ObjectMapper mapper;

    protected Logger logger = LoggerFactory.getLogger( ODMDevOpsIT.class);

    protected final String RESOURCE_ACTIVITY_1 = "src/test/resources/activity-1.json";
   
    @PostConstruct
    public final void init() {
        resourceBuilder = new ResourceBuilder();
        devOpsClient = new DevOpsClient("http://localhost:" + port);
    }

    @Before
    public void setup() {
        // objectMapper = DataProductDescriptor.buildObjectMapper();
    }

    @BeforeEach
    public void cleanDbState(@Autowired JdbcTemplate jdbcTemplate,  @Autowired Environment environment) {
        if(Arrays.stream(environment.getActiveProfiles()).findFirst().get().equals("testpostgresql")) {
            JdbcTestUtils.deleteFromTables(
                    jdbcTemplate,
                    "\"ODMDEVOPS\".\"ACTIVITIES\"",
                    "\"ODMDEVOPS\".\"TASKS\""
            );
        } else if (Arrays.stream(environment.getActiveProfiles()).findFirst().get().equals("testmysql")) {
            JdbcTestUtils.deleteFromTables(
                    jdbcTemplate,
                    "ODMDEVOPS.ACTIVITIES",
                    "ODMDEVOPS.TASKS"
            );
        }
    }

    // ======================================================================================
    // Create test basic resources
    // ======================================================================================

    protected ActivityResource createActivity(String filePath, boolean startAfterCreation) throws IOException {
        String payload = resourceBuilder.readResourceFromFile(filePath);
        ResponseEntity<ActivityResource> postActivityResponse = devOpsClient.postActivity(payload, startAfterCreation);
        verifyResponseEntity(postActivityResponse, HttpStatus.CREATED, true);
        return postActivityResponse.getBody();
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
            OpenDataMeshAPIStandardError error) {
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
