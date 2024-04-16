package org.opendatamesh.platform.pp.policy.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.opendatamesh.platform.core.commons.test.ODMIntegrationTest;
import org.opendatamesh.platform.core.commons.test.ODMResourceBuilder;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.policy.api.clients.PolicyClientImpl;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationRequestResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;
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

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.fail;

@ActiveProfiles("test")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {ODMPolicyApp.class})
public class ODMPolicyIT extends ODMIntegrationTest {

    @LocalServerPort
    protected String port;

    protected PolicyClientImpl policyClient;

    protected Logger logger = LoggerFactory.getLogger(ODMPolicyIT.class);

    protected final String DB_TABLES_POSTGRESQL = "src/test/resources/db/tables_postgresql.txt";

    protected final String DB_TABLES_MYSQL = "src/test/resources/db/tables_mysql.txt";

    @PostConstruct
    public void init() {
        mapper = ObjectMapperFactory.JSON_MAPPER;
        resourceBuilder = new ODMResourceBuilder(mapper);
        policyClient = new PolicyClientImpl("http://localhost:" + port, mapper);
    }

    @BeforeEach
    public void cleanDbState(
            @Autowired JdbcTemplate jdbcTemplate, @Autowired Environment environment
    ) throws IOException {
        String activeProfile = Arrays.stream(environment.getActiveProfiles()).findFirst().get();
        if (activeProfile.equals("testpostgresql")) {
            String[] tableSet = truncateAllTablesFromDb(jdbcTemplate, new File(DB_TABLES_POSTGRESQL));
            logger.debug("Postgres tables [" + tableSet + "] sucesfully truncated");
        } else if (activeProfile.equals("testmysql") || activeProfile.equals("localmysql")) {
            String[] tableSet = truncateAllTablesFromDb(jdbcTemplate, new File(DB_TABLES_MYSQL));
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
        PolicyEngineResource policyEngineResource = new PolicyEngineResource();
        policyEngineResource.setId(engineId);
        policyResource.setPolicyEngine(policyEngineResource);
        ResponseEntity<ObjectNode> postPolicyResponse = null;

        try {
            postPolicyResponse = policyClient.createPolicyResponseEntity(policyResource);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create policy: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(postPolicyResponse, HttpStatus.CREATED, true);
        policyResource = mapper.convertValue(postPolicyResponse.getBody(), PolicyResource.class);

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
        ResponseEntity<ObjectNode> postPolicyEngineResponse = null;

        try {
            postPolicyEngineResponse = policyClient.createPolicyEngineResponseEntity(policyEngineResource);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create policy engine: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(postPolicyEngineResponse, HttpStatus.CREATED, true);
        policyEngineResource = mapper.convertValue(postPolicyEngineResponse.getBody(), PolicyEngineResource.class);

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
        ResponseEntity<ObjectNode> postPolicyEvaluationResultResponse;

        try {
            postPolicyEvaluationResultResponse = policyClient.createPolicyEvaluationResultResponseEntity(policyEvaluationResultResource);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create policy evaluation result: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(postPolicyEvaluationResultResponse, HttpStatus.CREATED, true);
        policyEvaluationResultResource = mapper.convertValue(
                postPolicyEvaluationResultResponse.getBody(), PolicyEvaluationResultResource.class
        );

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

}
