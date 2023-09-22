package org.opendatamesh.platform.pp.devops.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.devops.api.resources.*;
import org.opendatamesh.platform.pp.devops.server.database.entities.Lifecycle;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class ActivityIT extends ODMDevOpsIT {

    // ======================================================================================
    // CREATE Activity
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivity() {

        createMocksForCreateActivityCall();

        ActivityResource activity = buildTestActivity();
        ActivityResource activity1 = createActivity(activity, false);

        assertThat(activity1.getId()).isNotNull();
        assertThat(activity1.getDataProductId()).isEqualTo(activity.getDataProductId());
        assertThat(activity1.getDataProductVersion()).isEqualTo(activity.getDataProductVersion());
        assertThat(activity1.getStage()).isEqualTo(activity.getStage());
        assertThat(activity1.getStatus()).isEqualTo(ActivityStatus.PLANNED);
        assertThat(activity1.getCreatedAt()).isNotNull();
        assertThat(activity1.getStartedAt()).isNull();
        assertThat(activity1.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateActivityAndStart() {

        createMocksForCreateActivityCall();

        ActivityResource activity = buildTestActivity();
        ActivityResource activity1 = createActivity(activity, true);

        assertThat(activity1.getId()).isNotNull();
        assertThat(activity1.getDataProductId()).isEqualTo(activity.getDataProductId());
        assertThat(activity1.getDataProductVersion()).isEqualTo(activity.getDataProductVersion());
        assertThat(activity1.getStage()).isEqualTo(activity.getStage());
        assertThat(activity1.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(activity1.getCreatedAt()).isNotNull();
        assertThat(activity1.getStartedAt()).isNotNull();
        assertThat(activity1.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateMultipleActivitiesOnSameDataProductVersion() {

        ActivityResource activity1 = null, activity2 = null;

        createMocksForCreateActivityCall();

        ActivityResource activity = buildTestActivity();
        activity.setStage("test");

        try {
            activity1 = devOpsClient.createActivity(activity, false);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }
        assertThat(activity1).isNotNull();

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activity1.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource searchedTaskRes = taskResources[0];
        assertThat(searchedTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);

        this.unbindMockServerFromRegistryClient();
        this.bindMockServerToRegistryClient();
        this.unbindMockServerFromExecutorClient();
        this.bindMockServerToExecutorClient();
        createMocksForCreateActivityCall();
        activity.setStage("prod");
        try {
            activity2 = devOpsClient.createActivity(activity, false);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }
        assertThat(activity2).isNotNull();

        taskResources = devOpsClient.searchTasks(activity2.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        searchedTaskRes = taskResources[0];
        assertThat(searchedTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);
    }

    // ======================================================================================
    // START/STOP Activity
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartActivity() {

        createMocksForCreateActivityCall();

        ActivityResource activity = buildTestActivity();

        ActivityResource activityRes = null;
        try {
            activityRes = devOpsClient.createActivity(activity, false);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        ActivityStatusResource statusRes = null;
        try {
            statusRes = devOpsClient.startActivity(activityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);


        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.readActivity(activityRes.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isEqualTo(activity.getDataProductId());
        assertThat(startedActivityRes.getDataProductVersion()).isEqualTo(activity.getDataProductVersion());
        assertThat(startedActivityRes.getStage()).isEqualTo(activity.getStage());
        assertThat(startedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(startedActivityRes.getCreatedAt()).isNotNull();
        assertThat(startedActivityRes.getStartedAt()).isNotNull();
        assertThat(startedActivityRes.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartActivityWithMissingTemplate() {
        createMocksForCreateActivityCall();

        ActivityResource activityRes = buildTestActivity();
        activityRes.setStage("stage-notemplate");

        ActivityResource createdActivityRes = null;
        try {
            createdActivityRes = devOpsClient.createActivity(activityRes, false);
        } catch (Throwable t) {
            fail("An unexpected exception occured while creating activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityStatusResource statusRes = null;
        try {
            statusRes = devOpsClient.startActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.readActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(startedActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(startedActivityRes.getStage()).isEqualTo("stage-notemplate");
        assertThat(startedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(startedActivityRes.getCreatedAt()).isNotNull();
        assertThat(startedActivityRes.getStartedAt()).isNotNull();
        assertThat(startedActivityRes.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartActivityWithMissingConfigurations() {
        createMocksForCreateActivityCall();

        ActivityResource activityRes = buildTestActivity();
        activityRes.setStage("stage-noconf");

        ActivityResource createdActivityRes = null;
        try {
            createdActivityRes = devOpsClient.createActivity(activityRes, false);
        } catch (Throwable t) {
            fail("An unexpected exception occured while creating activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityStatusResource statusRes = null;
        try {
            statusRes = devOpsClient.startActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.readActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }


        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(startedActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(startedActivityRes.getStage()).isEqualTo("stage-noconf");
        assertThat(startedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(startedActivityRes.getCreatedAt()).isNotNull();
        assertThat(startedActivityRes.getStartedAt()).isNotNull();
        assertThat(startedActivityRes.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartActivityWithMissingExecutor() {
        createMocksForCreateActivityCall();

        ActivityResource activityRes = buildTestActivity();
        activityRes.setStage("stage-noservice");

        ActivityResource createdActivityRes = null;
        try {
            createdActivityRes = devOpsClient.createActivity(activityRes, false);
        } catch (Throwable t) {
            fail("An unexpected exception occured while creating activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityStatusResource statusRes = null;
        try {
            statusRes = devOpsClient.startActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityStatus.PROCESSED);

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.readActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }

        ResponseEntity<LifecycleResource[]> lifecyclesGetResponse = devOpsClient.readLifecycles(LifecycleResource[].class);
        System.out.println(lifecyclesGetResponse.toString());

        ResponseEntity<LifecycleResource> lifecycleGetResponse = devOpsClient.readDataProductVersionCurrentLifecycle(
                startedActivityRes.getDataProductId(),
                startedActivityRes.getDataProductVersion(),
                LifecycleResource.class
        );

        LifecycleResource lifecycleResource = lifecycleGetResponse.getBody();

        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(startedActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(startedActivityRes.getStage()).isEqualTo("stage-noservice");
        assertThat(startedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
        assertThat(startedActivityRes.getCreatedAt()).isNotNull();
        assertThat(startedActivityRes.getStartedAt()).isNotNull();
        assertThat(startedActivityRes.getFinishedAt()).isNotNull();
        assertThat(lifecycleResource.getId()).isNotNull();
        assertThat(lifecycleResource.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(lifecycleResource.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(lifecycleResource.getStage()).isEqualTo("stage-noservice");
        assertThat(lifecycleResource.getStartedAt()).isNotNull();
        assertThat(lifecycleResource.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartActivityEmpty() {
        createMocksForCreateActivityCall();

        ActivityResource activityRes = buildTestActivity();
        activityRes.setStage("stage-empty");

        ActivityResource createdActivityRes = null;
        try {
            createdActivityRes = devOpsClient.createActivity(activityRes, false);
        } catch (Throwable t) {
            fail("An unexpected exception occured while creating activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityStatusResource statusRes = null;
        try {
            statusRes = devOpsClient.startActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityStatus.PROCESSED);

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.readActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }


        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(startedActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(startedActivityRes.getStage()).isEqualTo("stage-empty");
        assertThat(startedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
        assertThat(startedActivityRes.getCreatedAt()).isNotNull();
        assertThat(startedActivityRes.getStartedAt()).isNotNull();
        assertThat(startedActivityRes.getFinishedAt()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartActivityWithUnknownExecutor() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = buildTestActivity();
        activityRes.setStage("stage-wrong-executor");

        ActivityResource createdActivityRes = null;
        try {
            createdActivityRes = devOpsClient.createActivity(activityRes, false);
        } catch (Throwable t) {
            fail("An unexpected exception occured while creating activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

       ActivityStatusResource statusRes = null;
        try {
            statusRes = devOpsClient.startActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityStatus.FAILED);

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.readActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }


        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(startedActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(startedActivityRes.getStage()).isEqualTo("stage-wrong-executor");
        assertThat(startedActivityRes.getStatus()).isEqualTo(ActivityStatus.FAILED);
        assertThat(startedActivityRes.getCreatedAt()).isNotNull();
        assertThat(startedActivityRes.getStartedAt()).isNotNull();
        assertThat(startedActivityRes.getFinishedAt()).isNotNull();
    }

    // Note: An activity can be started but not directly stopped.
    // Activities are stopped when all its tasks are stopped
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStopActivity() {

        createMocksForCreateActivityCall();
        ActivityResource activityRes = buildTestActivity();
        ActivityResource createdActivityRes = createActivity(activityRes, true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(createdActivityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource targetTaskRes = taskResources[0];

        TaskStatusResource taskStatusRes = null;
        try {
            taskStatusRes = devOpsClient.stopTask(targetTaskRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while stopping task: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityResource stoppedActivityRes = devOpsClient.readActivity(createdActivityRes.getId());
        assertThat(stoppedActivityRes.getId()).isNotNull();
        assertThat(stoppedActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(stoppedActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(stoppedActivityRes.getStage()).isEqualTo("prod");
        assertThat(stoppedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
        assertThat(stoppedActivityRes.getResults()).matches(Pattern.compile("\\{\"\\d*\":\"OK\"\\}"));
        assertThat(stoppedActivityRes.getCreatedAt()).isNotNull();
        assertThat(stoppedActivityRes.getStartedAt()).isNotNull();
        assertThat(stoppedActivityRes.getFinishedAt()).isNotNull();
    }

    // ======================================================================================
    // READ Activity's status
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityStatusAfterCreate() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity(false);

        ActivityStatusResource statusRes = devOpsClient.readActivityStatus(activityRes.getId());
        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityStatus.PLANNED);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityStatusAfterCreateAndStart() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity(true);

        ActivityStatusResource statusRes = devOpsClient.readActivityStatus(activityRes.getId());
        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityStatusAfterStart() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity(false);

        ActivityStatusResource statusRes = null;
        try {
            statusRes = devOpsClient.startActivity(activityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        statusRes = devOpsClient.readActivityStatus(activityRes.getId());
        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityStatusAfterStop() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource targetTaskRes = taskResources[0];

        try {
            devOpsClient.stopTask(targetTaskRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while stopping task: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityStatusResource statusRes = devOpsClient.readActivityStatus(activityRes.getId());
        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
    }

    // ======================================================================================
    // READ Activity
    // ======================================================================================

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivities() throws IOException {

        createMocksForCreateActivityCall();

        // TEST 1: create first activity
        ActivityResource activityRes = null, createdActivityRes = null;

        activityRes = buildTestActivity();
        createdActivityRes = createActivity(activityRes, true);

        ActivityResource[] activityResources = devOpsClient.readAllActivities();
        assertThat(activityResources).isNotNull();
        assertThat(activityResources.length).isEqualTo(1);

        ActivityResource readActivityRes = activityResources[0];
        assertThat(readActivityRes.getId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(readActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(readActivityRes.getStage()).isEqualTo("prod");
        assertThat(readActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(readActivityRes.getCreatedAt()).isNotNull();
        assertThat(readActivityRes.getStartedAt()).isNotNull();
        assertThat(readActivityRes.getFinishedAt()).isNull();

        assertThat(readActivityRes).isEqualTo(createdActivityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityAfterCreate() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null, createdActivityRes = null;

        activityRes = buildTestActivity();
        createdActivityRes = createActivity(activityRes, false);

        ActivityResource readActivityRes = devOpsClient.readActivity(createdActivityRes.getId());
        assertThat(readActivityRes.getId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(readActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(readActivityRes.getStage()).isEqualTo("prod");
        assertThat(readActivityRes.getStatus()).isEqualTo(ActivityStatus.PLANNED);
        assertThat(readActivityRes.getCreatedAt()).isNotNull();
        assertThat(readActivityRes.getStartedAt()).isNull();
        assertThat(readActivityRes.getFinishedAt()).isNull();

        assertThat(readActivityRes).isEqualTo(createdActivityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityAfterCreateAndStart() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null, createdActivityRes = null;

        activityRes = buildTestActivity();
        createdActivityRes = createActivity(activityRes, true);

        ActivityResource readActivityRes = devOpsClient.readActivity(createdActivityRes.getId());
        assertThat(readActivityRes.getId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(readActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(readActivityRes.getStage()).isEqualTo("prod");
        assertThat(readActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(readActivityRes.getCreatedAt()).isNotNull();
        assertThat(readActivityRes.getStartedAt()).isNotNull();
        assertThat(readActivityRes.getFinishedAt()).isNull();

        assertThat(readActivityRes).isEqualTo(createdActivityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityAfterStart() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null, createdActivityRes = null;

        activityRes = buildTestActivity();
        createdActivityRes = createActivity(activityRes, false);

        ActivityStatusResource statusRes = null;
        try {
            statusRes = devOpsClient.startActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityResource readActivityRes = devOpsClient.readActivity(createdActivityRes.getId());
        assertThat(readActivityRes.getId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(readActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(readActivityRes.getStage()).isEqualTo("prod");
        assertThat(readActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(readActivityRes.getCreatedAt()).isNotNull();
        assertThat(readActivityRes.getStartedAt()).isNotNull();
        assertThat(readActivityRes.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityAfterStop() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null, createdActivityRes = null;

        activityRes = buildTestActivity();
        createdActivityRes = createActivity(activityRes, true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(createdActivityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource targetTaskRes = taskResources[0];

        TaskStatusResource stoppedTaskRes = null;
        try {
            stoppedTaskRes = devOpsClient.stopTask(targetTaskRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while stopping task: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityResource readActivityRes = devOpsClient.readActivity(createdActivityRes.getId());
        assertThat(readActivityRes.getId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(readActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(readActivityRes.getStage()).isEqualTo("prod");
        assertThat(readActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
        assertThat(readActivityRes.getCreatedAt()).isNotNull();
        assertThat(readActivityRes.getStartedAt()).isNotNull();
        assertThat(readActivityRes.getFinishedAt()).isNotNull();
    }

    // ======================================================================================
    // SEARCH Activity
    // ======================================================================================

    // TODO create multiple activities to be sure that the search call properly
    // filters results

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchActivityByDataProductId() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null, createdActivityRes = null;

        activityRes = buildTestActivity();
        createdActivityRes = createActivity(activityRes, true);

        ActivityResource[] activitiesResources = devOpsClient.searchActivities(createdActivityRes.getDataProductId(), null,
                null, null);
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activitiesResources[0];

        assertThat(searchedActivityRes.getId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(searchedActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(searchedActivityRes.getStage()).isEqualTo("prod");
        assertThat(searchedActivityRes.getStatus()).isNotNull();
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(searchedActivityRes.getCreatedAt()).isNotNull();
        assertThat(searchedActivityRes.getStartedAt()).isNotNull();
        assertThat(searchedActivityRes.getFinishedAt()).isNull();

        assertThat(searchedActivityRes).isEqualTo(createdActivityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchActivityByDataProductVersion() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null, createdActivityRes = null;

        activityRes = buildTestActivity();
        createdActivityRes = createActivity(activityRes, true);

        ActivityResource[] activitiesResources = devOpsClient.searchActivities(null,
                createdActivityRes.getDataProductVersion(), null, null);
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activitiesResources[0];

        assertThat(searchedActivityRes.getId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(searchedActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(searchedActivityRes.getStage()).isEqualTo("prod");
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(searchedActivityRes.getCreatedAt()).isNotNull();
        assertThat(searchedActivityRes.getStartedAt()).isNotNull();
        assertThat(searchedActivityRes.getFinishedAt()).isNull();

        assertThat(searchedActivityRes).isEqualTo(createdActivityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchActivityByType() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null, createdActivityRes = null;

        activityRes = buildTestActivity();
        createdActivityRes = createActivity(activityRes, true);

        ActivityResource[] activitiesResources = devOpsClient.searchActivities(null, null, createdActivityRes.getStage(), null);
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activitiesResources[0];

        assertThat(searchedActivityRes.getId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(searchedActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(searchedActivityRes.getStage()).isEqualTo("prod");
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(searchedActivityRes.getCreatedAt()).isNotNull();
        assertThat(searchedActivityRes.getStartedAt()).isNotNull();
        assertThat(searchedActivityRes.getFinishedAt()).isNull();

        assertThat(searchedActivityRes).isEqualTo(createdActivityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchActivityByStatus() {

        createMocksForCreateActivityCall();

       ActivityResource activityRes = null, createdActivityRes = null;

        activityRes = buildTestActivity();
        createdActivityRes = createActivity(activityRes, true);


        ActivityResource[] activitiesResources = devOpsClient.searchActivities(null, null, null,
                createdActivityRes.getStatus());
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activitiesResources[0];

        assertThat(searchedActivityRes.getId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(searchedActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(searchedActivityRes.getStage()).isEqualTo("prod");
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(searchedActivityRes.getCreatedAt()).isNotNull();
        assertThat(searchedActivityRes.getStartedAt()).isNotNull();
        assertThat(searchedActivityRes.getFinishedAt()).isNull();

        assertThat(searchedActivityRes).isEqualTo(createdActivityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchActivityByAll() {

        createMocksForCreateActivityCall();

       ActivityResource activityRes = null, createdActivityRes = null;

        activityRes = buildTestActivity();
        createdActivityRes = createActivity(activityRes, true);

        ActivityResource[] activitiesResources = devOpsClient.searchActivities(createdActivityRes.getDataProductId(),
                createdActivityRes.getDataProductVersion(), createdActivityRes.getStage(), createdActivityRes.getStatus());
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activitiesResources[0];

        assertThat(searchedActivityRes.getId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(searchedActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(searchedActivityRes.getStage()).isEqualTo("prod");
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(searchedActivityRes.getCreatedAt()).isNotNull();
        assertThat(searchedActivityRes.getStartedAt()).isNotNull();
        assertThat(searchedActivityRes.getFinishedAt()).isNull();

        assertThat(searchedActivityRes).isEqualTo(createdActivityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchActivityByNothing() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null, createdActivityRes = null;

        activityRes = buildTestActivity();
        createdActivityRes = createActivity(activityRes, true);
        
        ActivityResource[] activitiesResources = devOpsClient.searchActivities(null, null, null, null);
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activitiesResources[0];

       assertThat(searchedActivityRes.getId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isEqualTo(activityRes.getDataProductId());
        assertThat(searchedActivityRes.getDataProductVersion()).isEqualTo(activityRes.getDataProductVersion());
        assertThat(searchedActivityRes.getStage()).isEqualTo("prod");
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(searchedActivityRes.getCreatedAt()).isNotNull();
        assertThat(searchedActivityRes.getStartedAt()).isNotNull();
        assertThat(searchedActivityRes.getFinishedAt()).isNull();

        assertThat(searchedActivityRes).isEqualTo(createdActivityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchActivityMissing() {

        createMocksForCreateActivityCall();

       ActivityResource activityRes = null, createdActivityRes = null;

        activityRes = buildTestActivity();
        createdActivityRes = createActivity(activityRes, true);

        ActivityResource[] activitiesResources = devOpsClient.searchActivities("wrongProductId", "0.0.0", "StageX",
                null);
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(0);
    }
}