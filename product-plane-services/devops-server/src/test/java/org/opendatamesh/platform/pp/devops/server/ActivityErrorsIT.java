package org.opendatamesh.platform.pp.devops.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsAPIRoutes;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.opendatamesh.platform.pp.devops.api.resources.ErrorRes;
import org.opendatamesh.platform.pp.devops.api.resources.ODMDevOpsAPIStandardError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;


@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ActivityErrorsIT extends ODMDevOpsIT {

    // ======================================================================================
    // ERROR PATH
    // ======================================================================================

    // ----------------------------------------
    // CREATE Activity
    // ----------------------------------------

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityMissingProductId() {

        createMocksForCreateActivityCall();

        ActivityResource postedActivityRes = resourceBuilder.buildActivity(null, "1.0.0", "prod");
        ResponseEntity<ErrorRes> response = null;
        try {
            response = devOpsClient.postActivity(postedActivityRes, true, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        
        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.code());
        assertThat(errorRes.getDescription()).isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityMissingProductVersion() {

        createMocksForCreateActivityCall();

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("c18b07ba-bb01-3d55-a5bf-feb517a8d901", null, "prod");
        ResponseEntity<ErrorRes> response = null;
        try {
            response = devOpsClient.postActivity(postedActivityRes, true, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        
        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.code());
        assertThat(errorRes.getDescription()).isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityMissingType() {

        createMocksForCreateActivityCall();

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("c18b07ba-bb01-3d55-a5bf-feb517a8d901", "1.0.0", null);
        ResponseEntity<ErrorRes> response = null;
        try {
            response = devOpsClient.postActivity(postedActivityRes, true, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        
        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.code());
        assertThat(errorRes.getDescription()).isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    // TODO ricomincia da qui
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWrongType() {

        createMocksForCreateActivityCall();

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("c18b07ba-bb01-3d55-a5bf-feb517a8d901", "1.0.0", "xxx");
        ResponseEntity<ErrorRes> response = null;
        try {
            response = devOpsClient.postActivity(postedActivityRes, true, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        
        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.code());
        assertThat(errorRes.getDescription()).isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    
}