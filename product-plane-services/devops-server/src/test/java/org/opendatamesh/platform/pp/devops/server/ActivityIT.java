package org.opendatamesh.platform.pp.devops.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsAPIRoutes;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ErrorRes;
import org.opendatamesh.platform.pp.devops.api.resources.ODMDevOpsAPIStandardError;
import org.springframework.http.HttpStatus;
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
    public void testCreateActivityAndStart() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity1(true);
        assertThat(activityRes.getId()).isNotNull();
        assertThat(activityRes.getDataProductId()).isNotNull();
        assertThat(activityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(activityRes.getDataProductVersion()).isNotNull();
        assertThat(activityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(activityRes.getType()).isNotNull();
        assertThat(activityRes.getType()).isEqualTo("prod");
        assertThat(activityRes.getStatus()).isNotNull();
        assertThat(activityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(activityRes.getCreatedAt()).isNotNull();
        assertThat(activityRes.getStartedAt()).isNotNull();
        assertThat(activityRes.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testCreateMultipleActivitiesOnSameDataProductVersion() {

        ActivityResource firstCreatedActivityRes = null, secondCreatedActivityRes = null;
        createMocksForCreateActivityCall();
        ActivityResource postedActivityRes = resourceBuilder.buildActivity("c18b07ba-bb01-3d55-a5bf-feb517a8d901", "1.0.0", "qa");
        try {
            firstCreatedActivityRes = devOpsClient.createActivity(postedActivityRes, false);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }
        assertThat(firstCreatedActivityRes).isNotNull();

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(firstCreatedActivityRes.getId(), null, null) ;
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource searchedTaskRes = taskResources[0];
        assertThat(searchedTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);


        this.unbindMockServerFromRegistryClient();
        this.bindMockServerToRegistryClient();
        this.unbindMockServerFromExecutorClient();
        this.bindMockServerToExecutorClient();
        createMocksForCreateActivityCall();
        postedActivityRes = resourceBuilder.buildActivity("c18b07ba-bb01-3d55-a5bf-feb517a8d901", "1.0.0", "prod");
        try {
            secondCreatedActivityRes = devOpsClient.createActivity(postedActivityRes, false);
        } catch (Throwable t) {
            t.printStackTrace();
            fail("Impossible to post activity: " + t.getMessage());
            return;
        }
        assertThat(secondCreatedActivityRes).isNotNull();
        
        taskResources = devOpsClient.searchTasks(secondCreatedActivityRes.getId(), null, null) ;
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

        ActivityResource activityRes = createTestActivity1(false);

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.startActivity(activityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(startedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(startedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(startedActivityRes.getType()).isNotNull();
        assertThat(startedActivityRes.getType()).isEqualTo("prod");
        assertThat(startedActivityRes.getStatus()).isNotNull();
        assertThat(startedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(startedActivityRes.getCreatedAt()).isNotNull();
        assertThat(startedActivityRes.getStartedAt()).isNotNull();
        assertThat(startedActivityRes.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartActivityWithMissingTemplate() {
        createMocksForCreateActivityCall();
        
        ActivityResource activityRes = resourceBuilder.buildActivity(
            "c18b07ba-bb01-3d55-a5bf-feb517a8d901", 
            "1.0.0", 
            "stage-notemplate");


        ActivityResource createdActivityRes = null;
        try {
            createdActivityRes = devOpsClient.createActivity(activityRes, false);
        } catch (Throwable t) {
            fail("An unexpected exception occured while creating activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.startActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(startedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(startedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(startedActivityRes.getType()).isNotNull();
        assertThat(startedActivityRes.getType()).isEqualTo("stage-notemplate");
        assertThat(startedActivityRes.getStatus()).isNotNull();
        assertThat(startedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(startedActivityRes.getCreatedAt()).isNotNull();
        assertThat(startedActivityRes.getStartedAt()).isNotNull();
        assertThat(startedActivityRes.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartActivityWithMissingConfigurations() {
        createMocksForCreateActivityCall();
        
        ActivityResource activityRes = resourceBuilder.buildActivity(
            "c18b07ba-bb01-3d55-a5bf-feb517a8d901", 
            "1.0.0", 
            "stage-noconf");


        ActivityResource createdActivityRes = null;
        try {
            createdActivityRes = devOpsClient.createActivity(activityRes, false);
        } catch (Throwable t) {
            fail("An unexpected exception occured while creating activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.startActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(startedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(startedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(startedActivityRes.getType()).isNotNull();
        assertThat(startedActivityRes.getType()).isEqualTo("stage-noconf");
        assertThat(startedActivityRes.getStatus()).isNotNull();
        assertThat(startedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(startedActivityRes.getCreatedAt()).isNotNull();
        assertThat(startedActivityRes.getStartedAt()).isNotNull();
        assertThat(startedActivityRes.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartActivityWithMissingExecutor() {
        createMocksForCreateActivityCall();
        
        ActivityResource activityRes = resourceBuilder.buildActivity(
            "c18b07ba-bb01-3d55-a5bf-feb517a8d901", 
            "1.0.0", 
            "stage-noservice");


        ActivityResource createdActivityRes = null;
        try {
            createdActivityRes = devOpsClient.createActivity(activityRes, false);
        } catch (Throwable t) {
            fail("An unexpected exception occured while creating activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.startActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(startedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(startedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(startedActivityRes.getType()).isNotNull();
        assertThat(startedActivityRes.getType()).isEqualTo("stage-noservice");
        assertThat(startedActivityRes.getStatus()).isNotNull();
        assertThat(startedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
        assertThat(startedActivityRes.getCreatedAt()).isNotNull();
        assertThat(startedActivityRes.getStartedAt()).isNotNull();
        assertThat(startedActivityRes.getFinishedAt()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartActivityEmpty() {
        createMocksForCreateActivityCall();
        
        ActivityResource activityRes = resourceBuilder.buildActivity(
            "c18b07ba-bb01-3d55-a5bf-feb517a8d901", 
            "1.0.0", 
            "stage-empty");


        ActivityResource createdActivityRes = null;
        try {
            createdActivityRes = devOpsClient.createActivity(activityRes, false);
        } catch (Throwable t) {
            fail("An unexpected exception occured while creating activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.startActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(startedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(startedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(startedActivityRes.getType()).isNotNull();
        assertThat(startedActivityRes.getType()).isEqualTo("stage-empty");
        assertThat(startedActivityRes.getStatus()).isNotNull();
        assertThat(startedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
        assertThat(startedActivityRes.getCreatedAt()).isNotNull();
        assertThat(startedActivityRes.getStartedAt()).isNotNull();
        assertThat(startedActivityRes.getFinishedAt()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartActivityWithUnknownExecutor() {

        createMocksForCreateActivityCall();
        
        ActivityResource activityRes = resourceBuilder.buildActivity(
            "c18b07ba-bb01-3d55-a5bf-feb517a8d901", 
            "1.0.0", 
            "stage-wrong-executor");


        ActivityResource createdActivityRes = null;
        try {
            createdActivityRes = devOpsClient.createActivity(activityRes, false);
        } catch (Throwable t) {
            fail("An unexpected exception occured while creating activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.startActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(startedActivityRes.getId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isNotNull();
        assertThat(startedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(startedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(startedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(startedActivityRes.getType()).isNotNull();
        assertThat(startedActivityRes.getType()).isEqualTo("stage-wrong-executor");
        assertThat(startedActivityRes.getStatus()).isNotNull();
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

        ActivityResource stoppedActivityRes = devOpsClient.readActivity(activityRes.getId());
        assertThat(stoppedActivityRes.getId()).isNotNull();
        assertThat(stoppedActivityRes.getDataProductId()).isNotNull();
        assertThat(stoppedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(stoppedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(stoppedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(stoppedActivityRes.getType()).isNotNull();
        assertThat(stoppedActivityRes.getType()).isEqualTo("prod");
        assertThat(stoppedActivityRes.getStatus()).isNotNull();
        assertThat(stoppedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
        assertThat(stoppedActivityRes.getResults()).isNotNull();
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
        activityRes = createTestActivity1(false);

        String status = devOpsClient.readActivityStatus(activityRes.getId());
        assertThat(status).isNotNull();
        assertThat(status).isEqualTo(ActivityStatus.PLANNED.toString());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityStatusAfterCreateAndStart() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity1(true);

        String status = devOpsClient.readActivityStatus(activityRes.getId());
        assertThat(status).isNotNull();
        assertThat(status).isEqualTo(ActivityStatus.PROCESSING.toString());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityStatusAfterStart() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity1(false);

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.startActivity(activityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }
        String status = devOpsClient.readActivityStatus(startedActivityRes.getId());
        assertThat(status).isNotNull();
        assertThat(status).isEqualTo(ActivityStatus.PROCESSING.toString());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityStatusAfterStop() throws IOException {

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
        String status = devOpsClient.readActivityStatus(stoppedTaskRes.getId());
        assertThat(status).isNotNull();
        assertThat(status).isEqualTo(ActivityStatus.PROCESSED.toString());
    }

    // ======================================================================================
    // READ Activity
    // ======================================================================================
    
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivities() throws IOException {

        createMocksForCreateActivityCall();

        // TEST 1: create first activity
        ActivityResource activityRes = null, readActivityRes = null;
        activityRes = createTestActivity1(true);

        ActivityResource[] activityResources = devOpsClient.readAllActivities();
        assertThat(activityResources).isNotNull();
        assertThat(activityResources.length).isEqualTo(1);

        readActivityRes = activityResources[0];
        assertThat(readActivityRes.getId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(readActivityRes.getDataProductVersion()).isNotNull();
        assertThat(readActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(readActivityRes.getType()).isNotNull();
        assertThat(readActivityRes.getType()).isEqualTo("prod");
        assertThat(readActivityRes.getStatus()).isNotNull();
        assertThat(readActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(readActivityRes.getCreatedAt()).isNotNull();
        assertThat(readActivityRes.getStartedAt()).isNotNull();
        assertThat(readActivityRes.getFinishedAt()).isNull();

        assertThat(readActivityRes).isEqualTo(activityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityAfterCreate() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null, readActivityRes = null;
        activityRes = createTestActivity1(false);

        readActivityRes = devOpsClient.readActivity(activityRes.getId());
        assertThat(readActivityRes.getId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(readActivityRes.getDataProductVersion()).isNotNull();
        assertThat(readActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(readActivityRes.getType()).isNotNull();
        assertThat(readActivityRes.getType()).isEqualTo("prod");
        assertThat(readActivityRes.getStatus()).isNotNull();
        assertThat(readActivityRes.getStatus()).isEqualTo(ActivityStatus.PLANNED);
        assertThat(readActivityRes.getCreatedAt()).isNotNull();
        assertThat(readActivityRes.getStartedAt()).isNull();
        assertThat(readActivityRes.getFinishedAt()).isNull();

        assertThat(readActivityRes).isEqualTo(activityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityAfterCreateAndStart() throws IOException {

        createMocksForCreateActivityCall();

        // TEST 1: create first activity
        ActivityResource activityRes = null, readActivityRes = null;
        activityRes = createTestActivity1(true);

        readActivityRes = devOpsClient.readActivity(activityRes.getId());
        assertThat(readActivityRes.getId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(readActivityRes.getDataProductVersion()).isNotNull();
        assertThat(readActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(readActivityRes.getType()).isNotNull();
        assertThat(readActivityRes.getType()).isEqualTo("prod");
        assertThat(readActivityRes.getStatus()).isNotNull();
        assertThat(readActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(readActivityRes.getCreatedAt()).isNotNull();
        assertThat(readActivityRes.getStartedAt()).isNotNull();
        assertThat(readActivityRes.getFinishedAt()).isNull();

        assertThat(readActivityRes).isEqualTo(activityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityAfterStart() throws IOException {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity1(false);

        ActivityResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.startActivity(activityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occured while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityResource readActivityRes = devOpsClient.readActivity(startedActivityRes.getId());
        assertThat(readActivityRes.getId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(readActivityRes.getDataProductVersion()).isNotNull();
        assertThat(readActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(readActivityRes.getType()).isNotNull();
        assertThat(readActivityRes.getType()).isEqualTo("prod");
        assertThat(readActivityRes.getStatus()).isNotNull();
        assertThat(readActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(readActivityRes.getCreatedAt()).isNotNull();
        assertThat(readActivityRes.getStartedAt()).isNotNull();
        assertThat(readActivityRes.getFinishedAt()).isNull();

        assertThat(readActivityRes).isEqualTo(startedActivityRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadActivityAfterStop() throws IOException {

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

        ActivityResource readActivityRes = devOpsClient.readActivity(stoppedTaskRes.getId());
        assertThat(readActivityRes.getId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isNotNull();
        assertThat(readActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(readActivityRes.getDataProductVersion()).isNotNull();
        assertThat(readActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(readActivityRes.getType()).isNotNull();
        assertThat(readActivityRes.getType()).isEqualTo("prod");
        assertThat(readActivityRes.getStatus()).isNotNull();
        assertThat(readActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
        assertThat(readActivityRes.getCreatedAt()).isNotNull();
        assertThat(readActivityRes.getStartedAt()).isNotNull();
        assertThat(readActivityRes.getFinishedAt()).isNotNull();
    }

    // ======================================================================================
    // SEARCH Activity
    // ======================================================================================
    
    // TODO create multiple activities to be sure that the search call properly filters results

    @Test 
    @DirtiesContext(methodMode=MethodMode.AFTER_METHOD)
    public void testSearchActivityByDataProductId() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity1(true);

        ActivityResource[] activitiesResources = devOpsClient.searchActivities(activityRes.getDataProductId(), null, null, null);
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activitiesResources[0];

        assertThat(searchedActivityRes.getId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(searchedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(searchedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(searchedActivityRes.getType()).isNotNull();
        assertThat(searchedActivityRes.getType()).isEqualTo("prod");
        assertThat(searchedActivityRes.getStatus()).isNotNull();
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(searchedActivityRes.getCreatedAt()).isNotNull();
        assertThat(searchedActivityRes.getStartedAt()).isNotNull();
        assertThat(searchedActivityRes.getFinishedAt()).isNull();

        assertThat(searchedActivityRes).isEqualTo(activityRes);
    }

    @Test 
    @DirtiesContext(methodMode=MethodMode.AFTER_METHOD)
    public void testSearchActivityByDataProductVersion() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity1(true);

        ActivityResource[] activitiesResources = devOpsClient.searchActivities(null, activityRes.getDataProductVersion(), null, null);
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activitiesResources[0];

        assertThat(searchedActivityRes.getId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(searchedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(searchedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(searchedActivityRes.getType()).isNotNull();
        assertThat(searchedActivityRes.getType()).isEqualTo("prod");
        assertThat(searchedActivityRes.getStatus()).isNotNull();
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(searchedActivityRes.getCreatedAt()).isNotNull();
        assertThat(searchedActivityRes.getStartedAt()).isNotNull();
        assertThat(searchedActivityRes.getFinishedAt()).isNull();

        assertThat(searchedActivityRes).isEqualTo(activityRes);
    }

    @Test 
    @DirtiesContext(methodMode=MethodMode.AFTER_METHOD)
    public void testSearchActivityByType() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity1(true);

        ActivityResource[] activitiesResources = devOpsClient.searchActivities(null, null, activityRes.getType(), null);
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activitiesResources[0];

        assertThat(searchedActivityRes.getId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(searchedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(searchedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(searchedActivityRes.getType()).isNotNull();
        assertThat(searchedActivityRes.getType()).isEqualTo("prod");
        assertThat(searchedActivityRes.getStatus()).isNotNull();
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(searchedActivityRes.getCreatedAt()).isNotNull();
        assertThat(searchedActivityRes.getStartedAt()).isNotNull();
        assertThat(searchedActivityRes.getFinishedAt()).isNull();

        assertThat(searchedActivityRes).isEqualTo(activityRes);
    }

    @Test 
    @DirtiesContext(methodMode=MethodMode.AFTER_METHOD)
    public void testSearchActivityByStatus() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity1(true);

        ActivityResource[] activitiesResources = devOpsClient.searchActivities(null, null, null, activityRes.getStatus());
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activitiesResources[0];

        assertThat(searchedActivityRes.getId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(searchedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(searchedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(searchedActivityRes.getType()).isNotNull();
        assertThat(searchedActivityRes.getType()).isEqualTo("prod");
        assertThat(searchedActivityRes.getStatus()).isNotNull();
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(searchedActivityRes.getCreatedAt()).isNotNull();
        assertThat(searchedActivityRes.getStartedAt()).isNotNull();
        assertThat(searchedActivityRes.getFinishedAt()).isNull();

        assertThat(searchedActivityRes).isEqualTo(activityRes);
    }

    @Test 
    @DirtiesContext(methodMode=MethodMode.AFTER_METHOD)
    public void testSearchActivityByAll() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity1(true);

        ActivityResource[] activitiesResources = devOpsClient.searchActivities(activityRes.getDataProductId(), activityRes.getDataProductVersion(), activityRes.getType(), activityRes.getStatus());
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activitiesResources[0];

        assertThat(searchedActivityRes.getId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(searchedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(searchedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(searchedActivityRes.getType()).isNotNull();
        assertThat(searchedActivityRes.getType()).isEqualTo("prod");
        assertThat(searchedActivityRes.getStatus()).isNotNull();
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(searchedActivityRes.getCreatedAt()).isNotNull();
        assertThat(searchedActivityRes.getStartedAt()).isNotNull();
        assertThat(searchedActivityRes.getFinishedAt()).isNull();

        assertThat(searchedActivityRes).isEqualTo(activityRes);
    }

    @Test 
    @DirtiesContext(methodMode=MethodMode.AFTER_METHOD)
    public void testSearchActivityByNothing() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity1(true);

        ActivityResource[] activitiesResources = devOpsClient.searchActivities(null, null, null, null);
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(1);
        ActivityResource searchedActivityRes = activitiesResources[0];

        assertThat(searchedActivityRes.getId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isNotNull();
        assertThat(searchedActivityRes.getDataProductId()).isEqualTo("c18b07ba-bb01-3d55-a5bf-feb517a8d901");
        assertThat(searchedActivityRes.getDataProductVersion()).isNotNull();
        assertThat(searchedActivityRes.getDataProductVersion()).isEqualTo("1.0.0");
        assertThat(searchedActivityRes.getType()).isNotNull();
        assertThat(searchedActivityRes.getType()).isEqualTo("prod");
        assertThat(searchedActivityRes.getStatus()).isNotNull();
        assertThat(searchedActivityRes.getStatus()).isEqualTo(ActivityStatus.PROCESSING);
        assertThat(searchedActivityRes.getCreatedAt()).isNotNull();
        assertThat(searchedActivityRes.getStartedAt()).isNotNull();
        assertThat(searchedActivityRes.getFinishedAt()).isNull();

        assertThat(searchedActivityRes).isEqualTo(activityRes);
    }

    @Test 
    @DirtiesContext(methodMode=MethodMode.AFTER_METHOD)
    public void testSearchActivityMissing() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity1(true);

        ActivityResource[] activitiesResources = devOpsClient.searchActivities("wrongProductId", "0.0.0", "StageX", null);
        assertThat(activitiesResources).isNotNull();
        assertThat(activitiesResources.length).isEqualTo(0);
    }
}