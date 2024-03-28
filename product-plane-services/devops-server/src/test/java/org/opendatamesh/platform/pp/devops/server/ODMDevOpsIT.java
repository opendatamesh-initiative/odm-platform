package org.opendatamesh.platform.pp.devops.server;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.opendatamesh.platform.core.commons.clients.ODMIntegrationTest;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsClient;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.DevOpsApiStandardErrors;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsClients;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryAPIRoutes;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.opendatamesh.platform.up.executor.api.clients.ExecutorAPIRoutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.Nullable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.util.ObjectUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.fail;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

//@ActiveProfiles("test")
//@ActiveProfiles("testpostgresql")
//@ActiveProfiles("testmysql")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = { ODMDevOpsApp.class })
public abstract class ODMDevOpsIT extends ODMIntegrationTest{

    @LocalServerPort
    protected String port;

    protected DevOpsClient devOpsClient;
    protected ODMDevOpsResourceBuilder resourceBuilder;

    @Autowired
    DevOpsClients clients;

    protected ObjectMapper mapper;

    protected Logger logger = LoggerFactory.getLogger(ODMDevOpsIT.class);

    protected final String DB_TABLES_POSTGRESQL = "src/test/resources/db/tables_postgresql.txt";
    protected final String DB_TABLES_MYSQL = "src/test/resources/db/tables_mysql.txt";

    @PostConstruct
    public void init() {

        mapper = ObjectMapperFactory.JSON_MAPPER;
        resourceBuilder = new ODMDevOpsResourceBuilder();
        devOpsClient = new DevOpsClient("http://localhost:" + port);

        bindMockServerToRegistryClient();
        bindMockServerToExecutorClient();
  
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
    
    protected ActivityResource buildTestActivity() {
        return buildActivity(
            "f350cab5-992b-32f7-9c90-79bca1bf10be", 
            "1.0.0", "prod");
    }
    protected ActivityResource buildActivity(
        String dataProductId,
        String dataProductVersion,
        String stage) {
        
        ActivityResource activity = new ActivityResource();

        activity.setDataProductId(dataProductId);
        activity.setDataProductVersion(dataProductVersion);
        activity.setStage(stage);

        return activity;
    }

    protected ActivityResource createTestActivity(boolean startAfterCreation) {
        return createActivity(
                ODMDevOpsResources.RESOURCE_ACTIVITY_1, startAfterCreation);
    }

    protected ActivityResource createActivity(String filePath, boolean startAfterCreation) {
        ActivityResource createdActivityRes = null;

        ActivityResource activityRes;
        try {
            activityRes = resourceBuilder.readResourceFromFile(filePath, ActivityResource.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to read activity from file: " + t.getMessage());
            return null;
        }
        
        createdActivityRes = createActivity(activityRes, startAfterCreation);

        return createdActivityRes;
    }

    protected ActivityResource createActivity(ActivityResource activityRes, boolean startAfterCreation) {
        ActivityResource createdActivityRes = null;

        ResponseEntity<ActivityResource> postActivityResponse = null;
        
        try {
            postActivityResponse = devOpsClient.postActivity(activityRes, startAfterCreation);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to create activity: " + t.getMessage());
            return null;
        }

        verifyResponseEntity(postActivityResponse, HttpStatus.CREATED, true);
        createdActivityRes = postActivityResponse.getBody();

        return createdActivityRes;
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
            DevOpsApiStandardErrors error) {
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

    // ======================================================================================
    // MOCKS
    // ======================================================================================

    public void resetAllClientsMockServer() {
        resetRegistryClientMockServer();
        resetExecutorClientMockServer();
    }

    public void resetRegistryClientMockServer() {
        clients.getRegistryClient().resetMockServer();
    }

    public void resetExecutorClientMockServer() {
        clients.getExecutorClient("azure-devops").resetMockServer();
    }

    public void bindMockServerToRegistryClient() {
        clients.getRegistryClient().bindMockServer();
        /* 
        RestTemplate restTemplate = clients.getRegistryClient().getRest().getRestTemplate();
        registryClientRequestFactory = restTemplate.getRequestFactory();
        registryMockServer = MockRestServiceServer.bindTo(restTemplate)
                .ignoreExpectOrder(true)
                .build();
        */
    }

    public void unbindMockServerFromRegistryClient() {
        clients.getRegistryClient().unbindMockServer();
        //RestTemplate restTemplate = clients.getRegistryClient().getRest().getRestTemplate();
        //restTemplate.setRequestFactory(registryClientRequestFactory);
    }

    public void bindMockServerToExecutorClient() {
        clients.getExecutorClient("azure-devops").bindMockServer();
        /* 
        RestTemplate restTemplate =  clients.getExecutorClient("azure-devops").getRest().getRestTemplate();
        executorClientRequestFactory = restTemplate.getRequestFactory();
        executorMockServer = MockRestServiceServer.bindTo(restTemplate)
                .ignoreExpectOrder(true)
                .build();
        */
    }

    public void unbindMockServerFromExecutorClient() {
        clients.getExecutorClient("azure-devops").unbindMockServer();
        /* 
        RestTemplate restTemplate =  clients.getExecutorClient("azure-devops").getRest().getRestTemplate();
        restTemplate.setRequestFactory(executorClientRequestFactory);
        */
    }

    public void createMocksForCreateActivityCall() {
        createMocksForCreateActivityCall(true, true);
    }

    public void createMocksForCreateActivityCall(
            boolean mockRegistry, boolean mockExecutor) {
        try {
            if (mockRegistry) {
                String apiResponse = resourceBuilder.readResourceFromFile(ODMDevOpsResources.RESOURCE_DPV_1_CANONICAL);
                mockReadOneDataProductVersion(apiResponse, "f350cab5-992b-32f7-9c90-79bca1bf10be", "1.0.0");

                ExternalComponentResource templateRes = resourceBuilder.readResourceFromFile(
                        ODMDevOpsResources.TEMPLATE_DEF_1_CANONICAL,
                        ExternalComponentResource.class);
                mockReadOneTemplateDefinition(templateRes, "65f81a2f-65d8-3f03-9dff-ba598ba0292c");
            } else {
                unbindMockServerFromRegistryClient();
            }

            if (mockExecutor) {
                mockCreateTask();
            } else {
                unbindMockServerFromExecutorClient();
            }
        } catch (IOException e) {
            fail("Impossible to create moks");
            e.printStackTrace();
            return;
        }

    }

    public void mockReadOneDataProductVersion(String apiResponse, String dataProductId,
            String dataProductVersion) {
        logger.debug("  >>>  mockReadOneDataProductVersion");

        String apiUrl = clients.getRegistryClient().apiUrl(RegistryAPIRoutes.DATA_PRODUCTS,
                "/" + dataProductId + "/versions/" + dataProductVersion);

        /*
         * String apiResponse = null;
         * try {
         * apiResponse = mapper.writeValueAsString(dpvRes);
         * } catch (JsonProcessingException e) {
         * logger.error("Impossible to serialize data product version resource", e);
         * fail("Impossible to serialize data product version resource [" + dpvRes +
         * "]");
         * }
         */

        MediaType responseType = clients.getRegistryClient().getContentMediaType();

        // requestTo(apiUrl)
        try {
            clients.getRegistryClient().getMockServer()
                    .expect(ExpectedCount.twice(), new MyRequestMatcher(apiUrl))
                    // .andExpect(content().contentType(responseType))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(
                            withSuccess(apiResponse, MediaType.APPLICATION_JSON));
        } catch (Throwable t) {
            logger.error("Impossible to create mock for endpoint readOneDataProductVersion", t);
            fail("Impossible to create mock for endpoint readOneDataProductVersion with url [" + apiUrl + "]");
        }
    }

    public void mockReadOneTemplateDefinition(ExternalComponentResource templateRes, String templateId) {
        logger.debug("  >>>  mockReadOneTemplateDefinition");

        String apiUrl = clients.getRegistryClient().apiUrl(RegistryAPIRoutes.TEMPLATES,
                "/" + templateId);
        String apiResponse = null;

        try {
            apiResponse = mapper.writeValueAsString(templateRes);
        } catch (JsonProcessingException e) {
            logger.error("Impossible to serialize template def resource", e);
            fail("Impossible to serialize data product version resource [" + templateRes + "]");
        }
        MediaType responseType = clients.getRegistryClient().getContentMediaType();

        // requestTo(apiUrl)
        try {
            clients.getRegistryClient().getMockServer()
                    .expect(ExpectedCount.manyTimes(), new MyRequestMatcher(apiUrl))
                    // .andExpect(content().contentType(responseType))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(
                            withSuccess(apiResponse, MediaType.APPLICATION_JSON));
        } catch (Throwable t) {
            logger.error("Impossible to create mock for endpoint readOneTemplateDefinition", t);
            fail("Impossible to create mock for endpoint readOneDataProductVersion with url [" + apiUrl + "]");
        }
    }

    public void mockCreateTask() {
        logger.debug("  >>>  mockReadOneTemplateDefinition");

        String apiUrl = clients.getExecutorClient("azure-devops").apiUrl(ExecutorAPIRoutes.TASKS);

        // http://localhost:9003/api/v1/up/executor/tasks
        try {
            clients.getExecutorClient("azure-devops").getMockServer()
                    .expect(ExpectedCount.manyTimes(), requestTo(apiUrl))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(new MyResponseCreator());
        } catch (Throwable t) {
            logger.error("Impossible to create mock for endpoint readOneTemplateDefinition", t);
            fail("Impossible to create mock for endpoint readOneTemplateDefinition with url [" + apiUrl + "]");
        }
    }

    public class MyResponseCreator implements ResponseCreator {

        @Override
        public ClientHttpResponse createResponse(@Nullable ClientHttpRequest request) throws IOException {
            return withSuccess(request.getBody().toString(), MediaType.APPLICATION_JSON).createResponse(null);
        }

    }

    public class MyRequestMatcher implements RequestMatcher {

        String uriToMatch;

        public MyRequestMatcher(String uriToMatch) {
            this.uriToMatch = uriToMatch;
        }

        @Override
        public void match(ClientHttpRequest request) throws IOException, AssertionError {
            String requestedUri = request.getURI().toString();
            logger.debug("uriToMatch [" + uriToMatch + "]");
            logger.debug("requestedUri [" + requestedUri + "]");
            logger.debug("equals? [" + ObjectUtils.nullSafeEquals(requestedUri, uriToMatch) + "]");

            org.springframework.test.util.AssertionErrors.assertEquals("Unexpected request", uriToMatch, requestedUri);
        }

    }
}
