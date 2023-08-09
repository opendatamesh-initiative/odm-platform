package org.opendatamesh.platform.pp.devops.server;

import org.docx4j.openpackaging.io.Load;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsClient;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;


@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ActivityIT extends ODMDevOpsIT {
    /*
     * public void mockMetaServicePOST() throws JsonProcessingException {
     * logger.debug("    mockMetaServicePOST    ");
     * Load load = new Load();
     * load.setDataproductId(idDataProduct1v1);
     * load.setMetaServiceId("123");
     * load.setStatus(LoadStatus.DONE);
     * mockServer
     * .expect(ExpectedCount.once(),
     * requestTo(metaserviceaddress
     * + "/api/v1/planes/utility/meta-services/loads"))
     * .andExpect(content().contentType(MediaType.APPLICATION_JSON))
     * .andExpect(method(HttpMethod.POST))
     * .andRespond(withSuccess(mapper.writeValueAsString(load),
     * MediaType.APPLICATION_JSON));
     * }
     * 
     * public void mockMetaServicePUT() throws JsonProcessingException {
     * logger.debug("    mockMetaServicePUT    ");
     * Load load = new Load();
     * load.setDataproductId(idDataProduct1v1);
     * load.setMetaServiceId("123");
     * load.setStatus(LoadStatus.DONE);
     * mockServer
     * .expect(ExpectedCount.once(),
     * requestTo(metaserviceaddress
     * + "/api/v1/planes/utility/meta-services/loads"))
     * .andExpect(content().contentType(MediaType.APPLICATION_JSON))
     * .andExpect(method(HttpMethod.PUT))
     * .andRespond(withSuccess(mapper.writeValueAsString(load),
     * MediaType.APPLICATION_JSON));
     * }
     * 
     * public void mockMetaServiceDELETE(String dataProductId) {
     * logger.debug("    mockMetaServiceDELETE    ");
     * mockServer
     * .expect(ExpectedCount.once(),
     * requestTo(metaserviceaddress
     * + "/api/v1/planes/utility/meta-services/loads?dataProductId="
     * + dataProductId))
     * .andExpect(method(HttpMethod.DELETE))
     * .andRespond(withSuccess());
     * }
     */

    MockRestServiceServer mockServer;

    public void mockCreateActivity() {
        logger.debug("  >>>  mockPolicyResponse");

      
        mockServer
            .expect(ExpectedCount.once(),
                    requestTo("localhost"
                               + "/api/v1/planes/utility/policy-services/opa/validate?id=dataproduct"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(method(HttpMethod.POST))
            .andRespond(
                withSuccess(mapper.writeValueAsString(policyValidationResponse),
                    MediaType.APPLICATION_JSON));
      
    }

    @Before
    public void setup() {
       
        mockServer = MockRestServiceServer.bindTo(devOpsClient.getRest().getRestTemplate()).ignoreExpectOrder(true)
                .build();
        try {

            mockServer.expect(MockRestRequestMatchers.requestTo())
                    .andExpect(MockRestRequestMatchers.method(HttpMethod.GET))
                    .andRespond(MockRestResponseCreators.withStatus(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body("pippo"));

            // mockPolicyServicePOST(true);
            // mockMetaServicePOST();
        } catch (JsonProcessingException e) {
            logger.error("Impossible to setup mock server", e);
        }
        
    }

    // ======================================================================================
    // HAPPY PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Activity
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testActivityCreate()
            throws IOException {

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
}