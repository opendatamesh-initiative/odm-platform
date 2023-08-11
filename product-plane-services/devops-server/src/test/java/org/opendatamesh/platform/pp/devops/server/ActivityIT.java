package org.opendatamesh.platform.pp.devops.server;

import org.docx4j.openpackaging.io.Load;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsClient;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.server.ActivityIT.MyRequestMatcher;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsClients;
import org.opendatamesh.platform.pp.registry.api.v1.clients.RegistryAPIRoutes;
import org.opendatamesh.platform.pp.registry.api.v1.clients.RegistryClient;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DefinitionResource;
import org.opendatamesh.platform.up.executor.api.clients.ExecutorAPIRoutes;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.RequestMatcher;
import org.springframework.test.web.client.ResponseCreator;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;
import org.springframework.util.ObjectUtils;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

import javax.annotation.PostConstruct;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ActivityIT extends ODMDevOpsIT {

    // ======================================================================================
    // HAPPY PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Activity
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testActivityCreate() throws IOException {

        DataProductVersionDPDS dpvRes = resourceBuilder.readResourceFromFile(RESOURCE_DPV_1_CANONICAL,
                DataProductVersionDPDS.class);
                
        String apiResponse = resourceBuilder.readResourceFromFile(RESOURCE_DPV_1_CANONICAL);
        mockReadOneDataProductVersion(apiResponse, "c18b07ba-bb01-3d55-a5bf-feb517a8d901", "1.0.0");
        mockCreateTask();

        DefinitionResource templateRes = resourceBuilder.readResourceFromFile(TEMPLATE_DEF_1_CANONICAL,
                DefinitionResource.class);
        mockReadOneTemplateDefinition(templateRes, "1");

        // TEST 1: create first activity and start it
        ActivityResource activityRes = null;
        activityRes = createActivity(RESOURCE_ACTIVITY_1, true);
        assertThat(activityRes.getId()).isNotNull();
        assertThat(activityRes.getDataProductId()).isNotNull();
        assertThat(activityRes.getDataProductVersion()).isNotNull();
        assertThat(activityRes.getType()).isNotNull();
        assertThat(activityRes.getStatus()).isNotNull();
        assertThat(activityRes.getCreatedAt()).isNotNull();
        assertThat(activityRes.getStartedAt()).isNotNull();
    }

    // ----------------------------------------
    // READ Activity
    // ----------------------------------------
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testActivityReadAll() throws IOException {

        ResponseEntity<ActivityResource> postActivityResponse = null;
        ActivityResource dataActivityRequest;

        dataActivityRequest = resourceBuilder.buildActivity(
                "c18b07ba-bb01-3d55-a5bf-feb517a8d901",
                "1.0.0",
                "prod");
        postActivityResponse = devOpsClient.postActivity(dataActivityRequest, true);
        verifyResponseEntity(postActivityResponse, HttpStatus.CREATED, true);

        ResponseEntity<ActivityResource[]> getProducteResponse = devOpsClient.getActivities();
        verifyResponseEntity(getProducteResponse, HttpStatus.OK, true);
        assertThat(getProducteResponse.getBody().length).isEqualTo(1);

        // TODO test also content of each data product in the response body
    }

    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

    // TODO ...

    // ======================================================================================
    // MOCKS
    // ======================================================================================

    public void mockReadOneDataProductVersion(String apiResponse, String dataProductId,
            String dataProductVersion) {
        logger.debug("  >>>  mockReadOneDataProductVersion");

        String apiUrl = clients.getRegistryClient().apiUrl(RegistryAPIRoutes.DATA_PRODUCTS,
                "/" + dataProductId + "/versions/" + dataProductVersion);

        /*         
        String apiResponse = null;
        try {
            apiResponse = mapper.writeValueAsString(dpvRes);
        } catch (JsonProcessingException e) {
            logger.error("Impossible to serialize data product version resource", e);
            fail("Impossible to serialize data product version resource [" + dpvRes + "]");
        }
        */

        logger.debug(apiResponse);
        MediaType responseType = clients.getRegistryClient().getContentMediaType();

        // requestTo(apiUrl)
        try {
            registryMockServer
                    .expect(ExpectedCount.once(), new MyRequestMatcher(apiUrl))
                    //.andExpect(content().contentType(responseType))
                    .andExpect(method(HttpMethod.GET))
                    .andRespond(
                            withSuccess(apiResponse, MediaType.APPLICATION_JSON));
        } catch (Throwable t) {
            logger.error("Impossible to create mock for endpoint readOneDataProductVersion", t);
            fail("Impossible to create mock for endpoint readOneDataProductVersion with url [" + apiUrl + "]");
        }
    }

    //http://localhost:8001/api/v1/pp/registry/templates/1

    public void mockReadOneTemplateDefinition(DefinitionResource templateRes, String templateid) {
        logger.debug("  >>>  mockReadOneTemplateDefinition");

        String apiUrl = clients.getRegistryClient().apiUrl(RegistryAPIRoutes.TEMPLATES,
                "/" + templateid);
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
            registryMockServer
                    .expect(ExpectedCount.manyTimes(), new MyRequestMatcher(apiUrl))
                    //.andExpect(content().contentType(responseType))
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
            executorMockServer
                    .expect(ExpectedCount.manyTimes(), requestTo(apiUrl))
                    .andExpect(method(HttpMethod.POST))
                    .andRespond(new MyResponseCreator());
        } catch (Throwable t) {
            logger.error("Impossible to create mock for endpoint readOneTemplateDefinition", t);
            fail("Impossible to create mock for endpoint readOneTemplateDefinition with url [" + apiUrl + "]");
        }
    }


    public class MyResponseCreator implements  ResponseCreator {

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
            logger.debug("uriToMatch [" + uriToMatch+ "]");
            logger.debug("requestedUri [" + requestedUri+ "]");
            logger.debug("equals? [" + ObjectUtils.nullSafeEquals(requestedUri, uriToMatch)+ "]");

            
            org.springframework.test.util.AssertionErrors.assertEquals("Unexpected request", uriToMatch, requestedUri);
        }

    }
}