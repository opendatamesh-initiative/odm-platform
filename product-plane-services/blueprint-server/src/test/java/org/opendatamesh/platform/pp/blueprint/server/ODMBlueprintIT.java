package org.opendatamesh.platform.pp.blueprint.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.pp.blueprint.api.clients.BlueprintClient;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintApiStandardErrors;
import org.opendatamesh.platform.pp.blueprint.api.resources.BlueprintResource;
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
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

//@ActiveProfiles("test")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { ODMBlueprintApp.class })
public class ODMBlueprintIT {

    @LocalServerPort
    protected String port;

    protected BlueprintClient blueprintClient;

    protected ODMBlueprintResourceBuilder resourceBuilder;

    protected ObjectMapper mapper;

    protected Logger logger = LoggerFactory.getLogger(ODMBlueprintIT.class);

    protected final String DB_TABLES_POSTGRESQL = "src/test/resources/db/tables_postgresql.txt";

    protected final String DB_TABLES_MYSQL = "src/test/resources/db/tables_mysql.txt";

    @PostConstruct
    public void init() {

        mapper = ObjectMapperFactory.JSON_MAPPER;
        resourceBuilder = new ODMBlueprintResourceBuilder();
        blueprintClient = new BlueprintClient("http://localhost:" + port);

    }

    @BeforeEach
    public void cleanDbState(@Autowired JdbcTemplate jdbcTemplate, @Autowired Environment environment)
            throws IOException {
        String activeProfile = Arrays.stream(environment.getActiveProfiles()).findFirst().get();
        String[] tableSet;
        if (activeProfile.equals("testpostgresql")) {
            tableSet = Files.readAllLines(new File(DB_TABLES_POSTGRESQL).toPath(), Charset.defaultCharset())
                    .toArray(new String[0]);
            JdbcTestUtils.deleteFromTables(
                    jdbcTemplate,
                    tableSet);
            logger.debug("Postgres tables [" + tableSet + "] sucesfully truncated");
        } else if (activeProfile.equals("testmysql") || activeProfile.equals("localmysql")) {
            tableSet = Files.readAllLines(new File(DB_TABLES_MYSQL).toPath(), Charset.defaultCharset())
                    .toArray(new String[0]);
            JdbcTestUtils.deleteFromTables(
                    jdbcTemplate,
                    tableSet);
            logger.debug("Mysql tables [" + tableSet + "] sucesfully truncated");
        }
    }

    // ======================================================================================
    // Create test basic resources
    // ======================================================================================

    protected BlueprintResource createBlueprintResource(String filePath) {
        try {
            return resourceBuilder.readResourceFromFile(filePath, BlueprintResource.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read blueprint from file: " + t.getMessage());
            return null;
        }
    }

    protected BlueprintResource createBlueprint(String filePath) {

        BlueprintResource blueprintResource = createBlueprintResource(filePath);

        ResponseEntity<BlueprintResource> postBlueprintResponse = null;

        try {
            postBlueprintResponse = blueprintClient.createBlueprintNoCheck(blueprintResource);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create blueprint: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(postBlueprintResponse, HttpStatus.CREATED, true);
        blueprintResource = postBlueprintResponse.getBody();

        return blueprintResource;

    }

    // ======================================================================================
    // Verify test basic resources
    // ======================================================================================

    protected ResponseEntity verifyResponseEntity(ResponseEntity responseEntity, HttpStatus statusCode,
                                                  boolean checkBody) {
        assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(statusCode);
        if (checkBody) {
            AssertionsForClassTypes.assertThat(responseEntity.getBody()).isNotNull();
        }
        return responseEntity;
    }

    protected void verifyResponseError(
            ResponseEntity<ErrorRes> errorResponse,
            HttpStatus status,
            BlueprintApiStandardErrors error
    ) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getCode()).isEqualTo(error.code());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getDescription()).isEqualTo(error.description());
    }

    protected void verifyResponseError(
            ResponseEntity<ErrorRes> errorResponse,
            HttpStatus status,
            BlueprintApiStandardErrors error,
            String message
    ) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getCode()).isEqualTo(error.code());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getDescription()).isEqualTo(error.description());
        assertThat(errorResponse.getBody().getMessage()).isEqualTo(message);
    }

}
