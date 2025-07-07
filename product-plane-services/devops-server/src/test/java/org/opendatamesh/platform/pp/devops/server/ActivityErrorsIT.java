package org.opendatamesh.platform.pp.devops.server;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.core.commons.clients.resources.ErrorRes;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiCommonErrors;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsAPIRoutes;
import org.opendatamesh.platform.pp.devops.api.resources.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ActivityErrorsIT extends ODMDevOpsIT {

    // ======================================================================================
    // CREATE Activity
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWhenRegistryIsUnavailable() {

        createMocksForCreateActivityCall(false, true);

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be",
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
        assertThat(errorRes.getCode()).isEqualTo(ODMApiCommonErrors.SC500_50_REGISTRY_SERVICE_ERROR.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMApiCommonErrors.SC500_50_REGISTRY_SERVICE_ERROR.description());
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
        ActivityResource activityRes = createTestActivity(false);
        assertThat(activityRes.getId()).isNotNull();
        assertThat(activityRes.getDataProductId()).isNotNull();
        assertThat(activityRes.getDataProductId()).isEqualTo("f350cab5-992b-32f7-9c90-79bca1bf10be");
        assertThat(activityRes.getDataProductVersion()).isNotNull();
        assertThat(activityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(activityRes.getStage()).isNotNull();
        assertThat(activityRes.getStage()).isEqualTo("prod");
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
        createMocksForCreateActivityCall(true, false);
        // NOTE because the call to executor service fails the activity and relative
        // tasks are terminated with FAILED status
        ActivityResource activityRes = createTestActivity(true);
        assertThat(activityRes.getId()).isNotNull();
        assertThat(activityRes.getDataProductId()).isNotNull();
        assertThat(activityRes.getDataProductId()).isEqualTo("f350cab5-992b-32f7-9c90-79bca1bf10be");
        assertThat(activityRes.getDataProductVersion()).isNotNull();
        assertThat(activityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(activityRes.getStage()).isNotNull();
        assertThat(activityRes.getStage()).isEqualTo("prod");
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
                .isEqualTo(ODMApiCommonErrors.SC400_00_REQUEST_BODY_IS_NOT_READABLE.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMApiCommonErrors.SC400_00_REQUEST_BODY_IS_NOT_READABLE.description());
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
                .isEqualTo(ODMApiCommonErrors.SC400_00_REQUEST_BODY_IS_NOT_READABLE.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMApiCommonErrors.SC400_00_REQUEST_BODY_IS_NOT_READABLE.description());
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
                .isEqualTo(ODMApiCommonErrors.SC415_01_REQUEST_MEDIA_TYPE_NOT_SUPPORTED.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(ODMApiCommonErrors.SC415_01_REQUEST_MEDIA_TYPE_NOT_SUPPORTED.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityAcceptingUnsupportedMediaType() {
        createMocksForCreateActivityCall();

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be",
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
                .isEqualTo(ODMApiCommonErrors.SC406_01_REQUEST_ACCEPTED_MEDIA_TYPES_NOT_SUPPORTED.code());
        assertThat(errorRes.getDescription()).isEqualTo(
            ODMApiCommonErrors.SC406_01_REQUEST_ACCEPTED_MEDIA_TYPES_NOT_SUPPORTED.description());
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
        assertThat(errorRes.getCode()).isEqualTo(DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWithMissingProductVersion() {

        createMocksForCreateActivityCall();

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be", null,
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
        assertThat(errorRes.getCode()).isEqualTo(DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWithMissingType() {

        createMocksForCreateActivityCall();

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be",
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
        assertThat(errorRes.getCode()).isEqualTo(DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityWithWrongType() {

        createMocksForCreateActivityCall();

        ActivityResource postedActivityRes = resourceBuilder.buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be",
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
        assertThat(errorRes.getCode()).isEqualTo(DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateSameActivityMultipleTimes() {

        createMocksForCreateActivityCall();
        ActivityResource postedActivityRes = resourceBuilder.buildActivity("f350cab5-992b-32f7-9c90-79bca1bf10be",
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
        assertThat(errorRes.getCode()).isEqualTo(DevOpsApiStandardErrors.SC422_02_ACTIVITY_ALREADY_EXISTS.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(DevOpsApiStandardErrors.SC422_02_ACTIVITY_ALREADY_EXISTS.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo(
                "Activity for stage [prod] of version [1.0.0] of product [f350cab5-992b-32f7-9c90-79bca1bf10be] already exist");
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    // ======================================================================================
    // START/STOP Activity
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartNotExistingActivity() {

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
        assertThat(errorRes.getCode()).isEqualTo(DevOpsApiStandardErrors.SC404_01_ACTIVITY_NOT_FOUND.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(DevOpsApiStandardErrors.SC404_01_ACTIVITY_NOT_FOUND.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath())
                .isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath() + "/" + wrongActivityId + "/status");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartSameActivityMultipleTime() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        ActivityStatusResource statusRes = null;
        try {
            activityRes = createTestActivity(false);
            statusRes = devOpsClient.startActivity(activityRes.getId());
        } catch (Throwable t) {
            fail("Impossible to start activity " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);

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
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);

        ErrorRes errorRes = response.getBody();
        assertThat(errorRes).isNotNull();
        assertThat(errorRes.getCode()).isEqualTo(DevOpsApiStandardErrors.SC409_01_CONCURRENT_ACTIVITIES.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(DevOpsApiStandardErrors.SC409_01_CONCURRENT_ACTIVITIES.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath())
                .isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath() + "/" + activityRes.getId() + "/status");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartAlreadyProcessedActivity() {
        createMocksForCreateActivityCall();
        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource targetTaskRes = taskResources[0];

        TaskStatusResource statusRes = null;
        try {
            statusRes = devOpsClient.stopTask(targetTaskRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occurred while stopping task: " + t.getMessage());
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
        assertThat(errorRes.getCode()).isEqualTo(DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath())
                .isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath() + "/" + activityRes.getId() + "/status");
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    @Disabled // TO FIX
    public void testStartMultipleActivitiesOnSameDataProductVersion() {

        createMocksForCreateActivityCall();
     
        ActivityResource postedActivityRes = buildTestActivity();
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
            postedActivityRes.setStage("test");
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
        assertThat(errorRes.getCode()).isEqualTo(DevOpsApiStandardErrors.SC409_01_CONCURRENT_ACTIVITIES.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(DevOpsApiStandardErrors.SC409_01_CONCURRENT_ACTIVITIES.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getMessage()).isEqualTo(
                "There is already a running activity on version [f350cab5-992b-32f7-9c90-79bca1bf10be] of data product [1.0.0]");
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath());
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.CONFLICT.value());
        assertThat(errorRes.getTimestamp()).isNotNull();

        ActivityResource[] activityResources = devOpsClient.searchActivities("f350cab5-992b-32f7-9c90-79bca1bf10be",
                "1.0.0", "test", null);
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

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testActivityStatusFailedWhenAllTasksStartedIndividuallyAndFail() throws IOException {
        // Simulate multiple tasks and executor unavailable
        createMocksForCreateActivityWithMultipleTaskCall(true, false);
        createMocksForCreateActivityWithMultipleTaskCall(true, false);
        createMocksForCreateActivityWithMultipleTaskCall(true, false);

        // Create the activity (do not start it)
        ActivityResource activityRes = resourceBuilder.readResourceFromFile(
            ODMDevOpsResources.RESOURCE_ACTIVITY_1, ActivityResource.class);
        activityRes.setStage("prod");
        ActivityResource createdActivity = devOpsClient.createActivity(activityRes, false);
        assertThat(createdActivity.getStatus()).isEqualTo(ActivityStatus.PLANNED);

        // Start each task individually (simulate executor connection failure)
        ActivityTaskResource[] tasks = devOpsClient.searchTasks(createdActivity.getId(), null, null);
        assertThat(tasks).isNotNull();
        assertThat(tasks.length).isGreaterThanOrEqualTo(2);
        for (ActivityTaskResource task : tasks) {
            try {
                String url = "http://localhost:" + port + "/api/v1/pp/devops/tasks/" + task.getId() + "/status?action=START";
                devOpsClient.rest.exchange(
                    url,
                    org.springframework.http.HttpMethod.PATCH,
                    org.springframework.http.HttpEntity.EMPTY,
                    ActivityTaskResource.class
                );
            } catch (Throwable t) {
                // Expected: connection refused, task should be set to FAILED
            }
        }

        // After all tasks are failed, the activity should be FAILED
        ActivityResource updatedActivity = devOpsClient.readActivity(createdActivity.getId());
        assertThat(updatedActivity.getStatus()).isEqualTo(ActivityStatus.FAILED);
        assertThat(updatedActivity.getFinishedAt()).isNotNull();
        assertThat(updatedActivity.getErrors()).isNotNull();

        // All tasks should be FAILED
        ActivityTaskResource[] updatedTasks = devOpsClient.searchTasks(createdActivity.getId(), null, null);
        for (ActivityTaskResource task : updatedTasks) {
            assertThat(task.getStatus()).isEqualTo(ActivityTaskStatus.FAILED);
            assertThat(task.getErrors()).isNotNull();
            assertThat(task.getFinishedAt()).isNotNull();
        }
    }

    // ======================================================================================
    // READ Activity
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadNotExistingActivity() {

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
        assertThat(errorRes.getCode()).isEqualTo(DevOpsApiStandardErrors.SC404_01_ACTIVITY_NOT_FOUND.code());
        assertThat(errorRes.getDescription())
                .isEqualTo(DevOpsApiStandardErrors.SC404_01_ACTIVITY_NOT_FOUND.description());
        assertThat(errorRes.getMessage()).isNotNull();
        assertThat(errorRes.getPath()).isEqualTo(DevOpsAPIRoutes.ACTIVITIES.getPath() + "/" + wrongActivityId);
        assertThat(errorRes.getStatus()).isEqualTo(HttpStatus.NOT_FOUND.value());
        assertThat(errorRes.getTimestamp()).isNotNull();
    }
}