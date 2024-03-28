package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.opendatamesh.platform.core.commons.clients.ODMIntegrationTest;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
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

@ActiveProfiles("test")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {ODMPolicyApp.class})
public class ODMPolicyIT extends ODMIntegrationTest {

    @LocalServerPort
    protected String port;

    protected PolicyClientImpl policyClient;

    protected ODMPolicyResourceBuilder resourceBuilder;

    protected ObjectMapper mapper;

    protected Logger logger = LoggerFactory.getLogger(ODMPolicyIT.class);

    protected final String DB_TABLES_POSTGRESQL = "src/test/resources/db/tables_postgresql.txt";

    protected final String DB_TABLES_MYSQL = "src/test/resources/db/tables_mysql.txt";

    @PostConstruct
    public void init() {

        mapper = ObjectMapperFactory.JSON_MAPPER;
        resourceBuilder = new ODMPolicyResourceBuilder();
        policyClient = new PolicyClientImpl("http://localhost:" + port, mapper);

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

    protected PolicyResource createPolicyResource(String filePath) {
        try {
            return resourceBuilder.readResourceFromFile(filePath, PolicyResource.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read policy from file: " + t.getMessage());
            return null;
        }
    }

    protected PolicyResource createPolicy(String filePath, Long engineId) {

        PolicyResource policyResource = createPolicyResource(filePath);
        policyResource.setPolicyEngineId(engineId);

        ResponseEntity<PolicyResource> postPolicyResponse = null;

        try {
            postPolicyResponse = policyClient.createPolicyResponseEntity(policyResource);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create policy: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(postPolicyResponse, HttpStatus.CREATED, true);
        policyResource = postPolicyResponse.getBody();

        return policyResource;

    }

    protected PolicyEngineResource createPolicyEngineResource(String filePath) {
        try {
            return resourceBuilder.readResourceFromFile(filePath, PolicyEngineResource.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read policy engine from file: " + t.getMessage());
            return null;
        }
    }

    protected PolicyEngineResource createPolicyEngine(String filePath) {

        PolicyEngineResource policyEngineResource = createPolicyEngineResource(filePath);

        ResponseEntity<PolicyEngineResource> postPolicyEngineResponse = null;

        try {
            postPolicyEngineResponse = policyClient.createPolicyEngineResponseEntity(policyEngineResource);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create policy engine: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(postPolicyEngineResponse, HttpStatus.CREATED, true);
        policyEngineResource = postPolicyEngineResponse.getBody();

        return policyEngineResource;

    }

    protected PolicyEvaluationResultResource createPolicyEvaluationResultResource(String filePath) {
        try {
            return resourceBuilder.readResourceFromFile(filePath, PolicyEvaluationResultResource.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read policy evaluation result from file: " + t.getMessage());
            return null;
        }
    }

    protected PolicyEvaluationResultResource createPolicyEvaluationResult(String filePath, Long policyId) {

        PolicyEvaluationResultResource policyEvaluationResultResource = createPolicyEvaluationResultResource(filePath);
        policyEvaluationResultResource.setPolicyId(policyId);

        ResponseEntity<PolicyEvaluationResultResource> postPolicyEvaluationResultResponse = null;

        try {
            postPolicyEvaluationResultResponse = policyClient.createPolicyEvaluationResultResponseEntity(policyEvaluationResultResource);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create policy evaluation result: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(postPolicyEvaluationResultResponse, HttpStatus.CREATED, true);
        policyEvaluationResultResource = postPolicyEvaluationResultResponse.getBody();

        return policyEvaluationResultResource;

    }

    protected PolicyEvaluationRequestResource createPolicyEvaluationRequestResource(String filePath) {
        try {
            return resourceBuilder.readResourceFromFile(filePath, PolicyEvaluationRequestResource.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read policy evaluation request from file: " + t.getMessage());
            return null;
        }
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
            PolicyApiStandardErrors error
    ) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getCode()).isEqualTo(error.code());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getDescription()).isEqualTo(error.description());
    }

    protected void verifyResponseError(
            ResponseEntity<ErrorRes> errorResponse,
            HttpStatus status,
            PolicyApiStandardErrors error,
            String message
    ) {
        assertThat(errorResponse.getStatusCode()).isEqualByComparingTo(status);
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getCode()).isEqualTo(error.code());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getDescription()).isEqualTo(error.description());
        AssertionsForClassTypes.assertThat(errorResponse.getBody().getMessage()).isEqualTo(message);
    }

}
