package org.opendatamesh.platform.pp.notification.server;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.opendatamesh.platform.core.commons.test.ODMIntegrationTest;
import org.opendatamesh.platform.core.commons.test.ODMResourceBuilder;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.notification.api.clients.EventNotifierClientImpl;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.fail;

//@ActiveProfiles("test")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = {ODMEventNotifierApp.class})
public class ODMEventNotifierIT extends ODMIntegrationTest {

    @LocalServerPort
    protected String port;

    protected EventNotifierClientImpl eventNotifierClient;

    protected Logger logger = LoggerFactory.getLogger(ODMEventNotifierIT.class);

    protected final String DB_TABLES_POSTGRESQL = "src/test/resources/db/tables_postgresql.txt";

    protected final String DB_TABLES_MYSQL = "src/test/resources/db/tables_mysql.txt";

    @PostConstruct
    public void init() {
        mapper = ObjectMapperFactory.JSON_MAPPER;
        resourceBuilder = new ODMResourceBuilder(mapper);
        eventNotifierClient = new EventNotifierClientImpl("http://localhost:" + port, mapper);
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

    protected ObserverResource createObserverResource(String filePath) {
        try {
            return resourceBuilder.readResourceFromFile(filePath, ObserverResource.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read observer from file: " + t.getMessage());
            return null;
        }
    }

    protected ObserverResource createObserver(String filePath) {

        ObserverResource observerResource = createObserverResource(filePath);

        ResponseEntity<ObjectNode> postObserverResponse = null;

        try {
            postObserverResponse = eventNotifierClient.addObserverResponseEntity(observerResource);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create observer: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(postObserverResponse, HttpStatus.CREATED, true);
        observerResource = mapper.convertValue(postObserverResponse.getBody(), ObserverResource.class);

        return observerResource;

    }

}
