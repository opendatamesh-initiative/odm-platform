package org.opendatamesh.platform.pp.devops.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

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
    public void testActivityCreate() 
    throws IOException {

        // TEST 1: create first activity and start it
        ActivityResource activityRes = createActivity(RESOURCE_ACTIVITY_1, true);
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
    public void testDataProductReadAll() throws IOException {

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