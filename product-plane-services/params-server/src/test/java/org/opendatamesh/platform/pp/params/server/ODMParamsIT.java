package org.opendatamesh.platform.pp.params.server;

import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.pp.params.api.clients.ParamsClient;
import org.opendatamesh.platform.pp.params.api.resources.ParamResource;
import org.opendatamesh.platform.pp.params.api.resources.ParamsApiStandardErrors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

//@ActiveProfiles("dev")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { ODMParamsApp.class })
public class ODMParamsIT {

    @LocalServerPort
    protected String port;

    protected ParamsClient paramsClient;

    protected ParamsClient paramsClientWithDecryption;

    protected ODMParamsResourceBuilder resourceBuilder;

    protected Logger logger = LoggerFactory.getLogger(ODMParamsIT.class);

    protected final String DB_TABLES_POSTGRESQL = "src/test/resources/db/tables_postgresql.txt";

    protected final String DB_TABLES_MYSQL = "src/test/resources/db/tables_mysql.txt";

    @PostConstruct
    public void init() {

        resourceBuilder = new ODMParamsResourceBuilder();
        paramsClient = new ParamsClient("http://localhost:" + port);
        paramsClientWithDecryption = new ParamsClient("http://localhost:" + port, "paramclient123");

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

    protected ParamResource createParamResource(String filePath) {
        try {
            return resourceBuilder.readResourceFromFile(filePath, ParamResource.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read blueprint from file: " + t.getMessage());
            return null;
        }
    }

    protected ParamResource createParam(String filePath) {

        ParamResource paramResource = createParamResource(filePath);
        return createParam(paramResource);

    }

    protected ParamResource createParam(ParamResource paramResource) {

        ResponseEntity<ParamResource> postParamResponse = null;

        try {
            postParamResponse = paramsClient.createParam(paramResource);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create param: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(postParamResponse, HttpStatus.CREATED, true);
        paramResource = postParamResponse.getBody();

        return paramResource;

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
            ParamsApiStandardErrors error
    ) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getCode()).isEqualTo(error.code());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getDescription()).isEqualTo(error.description());
    }

    protected void verifyResponseError(
            ResponseEntity<ErrorRes> errorResponse,
            HttpStatus status,
            ParamsApiStandardErrors error,
            String message
    ) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getCode()).isEqualTo(error.code());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getDescription()).isEqualTo(error.description());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getMessage()).isEqualTo(message);
    }

}
