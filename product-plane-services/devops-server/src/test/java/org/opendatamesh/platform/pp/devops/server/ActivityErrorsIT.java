package org.opendatamesh.platform.pp.devops.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsAPIRoutes;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ErrorRes;
import org.opendatamesh.platform.pp.devops.api.resources.ODMDevOpsAPIStandardError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ActivityErrorsIT extends ODMDevOpsIT {

    // ======================================================================================
    // CREATE Activity
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWhenRegistryIsUnavailable() {

        createMocksForCreateActivityCall(false, true);

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("c18b07ba-bb01-3d55-a5bf-feb517a8d901",
                "1.0.0", "prod");
        ResponseEntity<ErrorRes> response = null;
        try {
            response = devOpsClient.postActivity(postedActivityRes, true, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(ODMDevOpsAPIStandardError.SC500_50_REGISTRY_SERVICE_ERROR.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC500_50_REGISTRY_SERVICE_ERROR.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWhenExecutorIsUnavailable() {

        createMocksForCreateActivityCall(true, false);

        // NOTE because activity is just created and not started executor service should
        // not be called
        ActivityResource activityRes = createTestActivity1(false);
        assertThat(activityRes.getId()).isNotNull();
        assertThat(activityRes.getDataProductId()).isNotNull();
        assertThat(activityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(activityRes.getDataProductVersion()).isNotNull();
        assertThat(activityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(activityRes.getType()).isNotNull();
        assertThat(activityRes.getType()).isEqualTo("prod");
        assertThat(activityRes.getStatus()).isNotNull();
        assertThat(activityRes.getStatus()).isEqualTo(ActivityStatus.PLANNED);
        assertThat(activityRes.getCreatedAt()).isNotNull();
        assertThat(activityRes.getStartedAt()).isNull();
        assertThat(activityRes.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateAndStartActivityWhenExecutorIsUnavailable() {

        createMocksForCreateActivityCall(true, false);

        // NOTE because the call to executor service fails the activity and relative
        // tasks are terminated with FAILED status
        ActivityResource activityRes = createTestActivity1(true);
        assertThat(activityRes.getId()).isNotNull();
        assertThat(activityRes.getDataProductId()).isNotNull();
        assertThat(activityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(activityRes.getDataProductVersion()).isNotNull();
        assertThat(activityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(activityRes.getType()).isNotNull();
        assertThat(activityRes.getType()).isEqualTo("prod");
        assertThat(activityRes.getStatus()).isNotNull();
        assertThat(activityRes.getStatus()).isEqualTo(ActivityStatus.FAILED);
        assertThat(activityRes.getErrors()).isNotNull();
        assertThat(activityRes.getCreatedAt()).isNotNull();
        assertThat(activityRes.getStartedAt()).isNotNull();
        assertThat(activityRes.getFinishedAt()).isNotNull();

        // TODO Check that also associated task is failed
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWithMissingPayload() {
        createMocksForCreateActivityCall();

        ResponseEntity<ErrorRes> response = null;
        try {
            response = devOpsClient.postActivity(null, true, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(ODMDevOpsAPIStandardError.SC400_00_REQUEST_BODY_IS_NOT_READABLE.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC400_00_REQUEST_BODY_IS_NOT_READABLE.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWithMalformedJsonPayload() {
        createMocksForCreateActivityCall();

        ResponseEntity<ErrorRes> response = null;
        try {
            response = devOpsClient.postActivity("This is not a valid json doc", true, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(ODMDevOpsAPIStandardError.SC400_00_REQUEST_BODY_IS_NOT_READABLE.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC400_00_REQUEST_BODY_IS_NOT_READABLE.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.BAD_REQUEST.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWithUnsupportedMediaType() {
        createMocksForCreateActivityCall();

        // ActivityResource postedActivityRes = resourceBuilder.buildActivity(null,
        // "1.0.0", "prod");
        ResponseEntity<ErrorRes> response = null;
        try {
            devOpsClient.setContentMediaType(MediaType.TEXT_PLAIN);
            response = devOpsClient.postActivity("This is a text/plain message", true, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        } finally {
            devOpsClient.setContentMediaType(MediaType.APPLICATION_JSON);
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(ODMDevOpsAPIStandardError.SC415_01_REQUEST_MEDIA_TYPE_NOT_SUPPORTED.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC415_01_REQUEST_MEDIA_TYPE_NOT_SUPPORTED.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityAcceptingUnsupportedMediaType() {
        createMocksForCreateActivityCall();

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("c18b07ba-bb01-3d55-a5bf-feb517a8d901",
                "1.0.0", "prod");
        ResponseEntity<ErrorRes> response = null;
        try {
            devOpsClient.setAcceptMediaType(MediaType.APPLICATION_PDF);
            response = devOpsClient.postActivity(postedActivityRes, true, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        } finally {
            devOpsClient.setAcceptMediaType(MediaType.APPLICATION_JSON);
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_ACCEPTABLE);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode())
                .isEqualTo(ODMDevOpsAPIStandardError.SC406_01_REQUEST_ACCEPTED_MEDIA_TYPES_NOT_SUPPORTED.code());
        assertThat(errorRes.getDescription()).isEqualTo(
                ODMDevOpsAPIStandardError.SC406_01_REQUEST_ACCEPTED_MEDIA_TYPES_NOT_SUPPORTED.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.NOT_ACCEPTABLE.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWithMissingProductId() {

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
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWithMissingProductVersion() {

        createMocksForCreateActivityCall();

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("c18b07ba-bb01-3d55-a5bf-feb517a8d901", null,
                "prod");
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
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWithMissingType() {

        createMocksForCreateActivityCall();

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("c18b07ba-bb01-3d55-a5bf-feb517a8d901",
                "1.0.0", null);
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
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWithWrongType() {

        createMocksForCreateActivityCall();

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("c18b07ba-bb01-3d55-a5bf-feb517a8d901",
                "1.0.0", "xxx");
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
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateSameActivityMultipleTimes() {

        createMocksForCreateActivityCall();
        ActivityResource postedActivityRes = resourceBuilder.buildActivity("c18b07ba-bb01-3d55-a5bf-feb517a8d901",
                "1.0.0", "prod");
        try {
            ActivityResource firstCreatedActivityRes = devOpsClient.createActivity(postedActivityRes, false);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        // create the same activity again....
        resetRegistryClientMockServer();
        createMocksForCreateActivityCall();
        ResponseEntity<ErrorRes> response = null;
        try {
            response = devOpsClient.postActivity(postedActivityRes, false, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(ODMDevOpsAPIStandardError.SC422_02_ACTIVITY_ALREADY_EXISTS.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC422_02_ACTIVITY_ALREADY_EXISTS.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo(
                "Activity for stage [prod] of version [1.0.0] of product [c18b07ba-bb01-3d55-a5bf-feb517a8d901] already exist");
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    // ======================================================================================
    // START/STOP Activity
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartActivityWithWrongId() {

        Long wrongActivityId = 50L;
        ResponseEntity<ErrorRes> response = null;
        try {
            response = devOpsClient.patchActivityStart(wrongActivityId, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to get activity: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(ODMDevOpsAPIStandardError.SC404_01_ACTIVITY_NOT_FOUND.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC404_01_ACTIVITY_NOT_FOUND.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath())
                .isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath() + "/" + wrongActivityId + "/start");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartSameActivityMultipleTime() {

        createMocksForCreateActivityCall();

        ActivityResource startedActivityRes = null;
        try {
            ActivityResource activityRes = createTestActivity1(false);
            startedActivityRes = devOpsClient.startActivity(activityRes.getId());
        } catch (Throwable t) {
            fail("Impossible to start activity " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(startedActivityRes).isNotNull();
        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);

        resetAllClientsMockServer();
        createMocksForCreateActivityCall();

        ResponseEntity<ErrorRes> response = null;
        try {
            response = devOpsClient.patchActivityStart(startedActivityRes.getId(), ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to start activity: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(ODMDevOpsAPIStandardError.SC409_01_CONCURRENT_ACTIVITIES.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC409_01_CONCURRENT_ACTIVITIES.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath())
                .isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath() + "/" + startedActivityRes.getId() + "/start");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartAlreadyProcessedActivity() {
        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity1(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource targetTaskRes = taskResources[0];

        ActivityTaskResource stoppedTaskRes = null;
        try {
            stoppedTaskRes = devOpsClient.stopTask(targetTaskRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while stopping task: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        resetAllClientsMockServer();
        createMocksForCreateActivityCall();

        ResponseEntity<ErrorRes> response = null;
        try {
            response = devOpsClient.patchActivityStart(activityRes.getId(), ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to start activity: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath())
                .isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath() + "/" + activityRes.getId() + "/start");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartMultipleActivitiesOnSameDataProductVersion() {

        createMocksForCreateActivityCall();
        ActivityResource postedActivityRes = resourceBuilder.buildActivity("c18b07ba-bb01-3d55-a5bf-feb517a8d901",
                "1.0.0", "qa");
        try {
            ActivityResource firstCreatedActivityRes = devOpsClient.createActivity(postedActivityRes, true);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        resetAllClientsMockServer();
        createMocksForCreateActivityCall();
        ResponseEntity<ErrorRes> response = null;
        try {
            postedActivityRes.setType("prod");
            response = devOpsClient.postActivity(postedActivityRes, true, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }
        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(ODMDevOpsAPIStandardError.SC409_01_CONCURRENT_ACTIVITIES.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC409_01_CONCURRENT_ACTIVITIES.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo(
                "There is already a running activity on version [c18b07ba-bb01-3d55-a5bf-feb517a8d901] of data product [1.0.0]");
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(errorRes.getTimestamp()).isNotNull();

        ActivityResource[] activityResources = devOpsClient.searchActivities("c18b07ba-bb01-3d55-a5bf-feb517a8d901",
                "1.0.0", "prod", null);
        assertThat(activityResources).isNotNull();
        assertThat(activityResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activityResources[0];
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PLANNED);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(searchedActivityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource searchedTaskRes = taskResources[0];
        assertThat(searchedTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);
    }

    // ======================================================================================
    // READ Activity
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityWithWrongId() {

        Long wrongActivityId = 50L;
        ResponseEntity<ErrorRes> response = null;
        try {
            response = devOpsClient.getActivity(wrongActivityId, ErrorRes.class);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to get activity: " + t.getMessage());
            return;
        }

        assertThat(response).isNotNull();
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(ODMDevOpsAPIStandardError.SC404_01_ACTIVITY_NOT_FOUND.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMDevOpsAPIStandardError.SC404_01_ACTIVITY_NOT_FOUND.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath() + "/" + wrongActivityId);
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }
}