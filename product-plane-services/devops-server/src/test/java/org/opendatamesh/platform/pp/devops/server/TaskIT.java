package org.opendatamesh.platform.pp.devops.server;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

@DirtiesContext(classMode = ClassMode.BEFORE_CLASS)
public class TaskIT extends ODMDevOpsIT {

    // ======================================================================================
    // CREATE Activity
    // ======================================================================================

    // Note: Task cannot be created directly.
    // Tasks are created when the parent activity is created

    // ======================================================================================
    // START/STOP Task
    // ======================================================================================

    // Note: A task can be stopped but not directly started.
    // Tasks are started when the parent activity is started
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStartTask() {

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

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(startedActivityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource taskRes = taskResources[0];

        ActivityTaskResource readTaskRes = devOpsClient.readTask(taskRes.getId());
        assertThat(readTaskRes).isNotNull();
        assertThat(readTaskRes.getId()).isNotNull();
        assertThat(readTaskRes.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(readTaskRes.getTemplate()).isEqualTo("{\"pipeline\":\"dpdLifecyclePipe\",\"version\":\"1.0.0\"}");
        assertThat(readTaskRes.getConfigurations()).isEqualTo("{\"stage\":\"PROD\"}");
        assertThat(readTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(readTaskRes.getResults()).isNull();
        assertThat(readTaskRes.getErrors()).isNull();
        assertThat(readTaskRes.getCreatedAt()).isNotNull();
        assertThat(readTaskRes.getStartedAt()).isNotNull();
        assertThat(readTaskRes.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStopTask() {

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
        assertThat(stoppedTaskRes).isNotNull();
        assertThat(stoppedTaskRes.getId()).isNotNull();
        assertThat(stoppedTaskRes.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(stoppedTaskRes.getTemplate()).isEqualTo("{\"pipeline\":\"dpdLifecyclePipe\",\"version\":\"1.0.0\"}");
        assertThat(stoppedTaskRes.getConfigurations()).isEqualTo("{\"stage\":\"PROD\"}");
        assertThat(stoppedTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSED);
        assertThat(stoppedTaskRes.getResults()).isNotNull();
        assertThat(stoppedTaskRes.getResults()).isEqualTo("OK");
        assertThat(stoppedTaskRes.getErrors()).isNull();
        assertThat(stoppedTaskRes.getCreatedAt()).isNotNull();
        assertThat(stoppedTaskRes.getStartedAt()).isNotNull();
        assertThat(stoppedTaskRes.getFinishedAt()).isNotNull();
    }


    // ======================================================================================
    // READ Task's status
    // ======================================================================================
    
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTaskStatusAfterCreate() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity1(false);

        ActivityTaskResource[] tasks = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(tasks).isNotNull();
        assertThat(tasks.length).isEqualTo(1);
        ActivityTaskResource taskRes = tasks[0];

        String status = devOpsClient.readTaskStatus(taskRes.getId());
        assertThat(status).isNotNull();
        assertThat(status).isEqualTo(ActivityTaskStatus.PLANNED.toString());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTaskStatusAfterCreateAndStart() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity1(true);

        ActivityTaskResource[] tasks = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(tasks).isNotNull();
        assertThat(tasks.length).isEqualTo(1);
        ActivityTaskResource taskRes = tasks[0];

        String status = devOpsClient.readTaskStatus(taskRes.getId());
        assertThat(status).isNotNull();
        assertThat(status).isEqualTo(ActivityTaskStatus.PROCESSING.toString());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTaskStatusAfterStart() {

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

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(startedActivityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource taskRes = taskResources[0];

        String status = devOpsClient.readTaskStatus(taskRes.getId());
        assertThat(status).isNotNull();
        assertThat(status).isEqualTo(ActivityTaskStatus.PROCESSING.toString());
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTaskStatusAfterStop() {

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

        String status = devOpsClient.readTaskStatus(stoppedTaskRes.getId());
        assertThat(status).isNotNull();
        assertThat(status).isEqualTo(ActivityTaskStatus.PROCESSED.toString());
    }


    // ======================================================================================
    // READ task
    // ======================================================================================
    
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTasks() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity1(true);

        ActivityTaskResource[] tasks = devOpsClient.readAllTasks();
        assertThat(tasks).isNotNull();
        assertThat(tasks.length).isEqualTo(1);
        ActivityTaskResource task = tasks[0];
        assertThat(task).isNotNull();
        assertThat(task.getId()).isNotNull();
        assertThat(task.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(task.getTemplate()).isEqualTo("{\"pipeline\":\"dpdLifecyclePipe\",\"version\":\"1.0.0\"}");
        assertThat(task.getConfigurations()).isEqualTo("{\"stage\":\"PROD\"}");
        assertThat(task.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(task.getResults()).isNull();
        assertThat(task.getErrors()).isNull();
        assertThat(task.getCreatedAt()).isNotNull();
        assertThat(task.getStartedAt()).isNotNull();
        assertThat(task.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTaskAfterCreate() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity1(false);

        ActivityTaskResource[] tasks = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(tasks).isNotNull();
        assertThat(tasks.length).isEqualTo(1);
        ActivityTaskResource taskRes = tasks[0];

        ActivityTaskResource readTaskRes = devOpsClient.readTask(taskRes.getId());
        assertThat(readTaskRes).isNotNull();
        assertThat(readTaskRes.getId()).isNotNull();
        assertThat(readTaskRes.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(readTaskRes.getTemplate()).isEqualTo("{\"pipeline\":\"dpdLifecyclePipe\",\"version\":\"1.0.0\"}");
        assertThat(readTaskRes.getConfigurations()).isEqualTo("{\"stage\":\"PROD\"}");
        assertThat(readTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);
        assertThat(readTaskRes.getResults()).isNull();
        assertThat(readTaskRes.getErrors()).isNull();
        assertThat(readTaskRes.getCreatedAt()).isNotNull();
        assertThat(readTaskRes.getStartedAt()).isNull();
        assertThat(readTaskRes.getFinishedAt()).isNull();

        assertThat(readTaskRes).isEqualTo(taskRes);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTaskAfterCreateAndStart() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity1(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource taskRes = taskResources[0];

        ActivityTaskResource readTaskRes = devOpsClient.readTask(taskRes.getId());
        assertThat(readTaskRes).isNotNull();
        assertThat(readTaskRes.getId()).isNotNull();
        assertThat(readTaskRes.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(readTaskRes.getTemplate()).isEqualTo("{\"pipeline\":\"dpdLifecyclePipe\",\"version\":\"1.0.0\"}");
        assertThat(readTaskRes.getConfigurations()).isEqualTo("{\"stage\":\"PROD\"}");
        assertThat(readTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(readTaskRes.getResults()).isNull();
        assertThat(readTaskRes.getErrors()).isNull();
        assertThat(readTaskRes.getCreatedAt()).isNotNull();
        assertThat(readTaskRes.getStartedAt()).isNotNull();
        assertThat(readTaskRes.getFinishedAt()).isNull();
    }


    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTaskAfterStart() {

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

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(startedActivityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource taskRes = taskResources[0];

        ActivityTaskResource readTaskRes = devOpsClient.readTask(taskRes.getId());
        assertThat(readTaskRes).isNotNull();
        assertThat(readTaskRes.getId()).isNotNull();
        assertThat(readTaskRes.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(readTaskRes.getTemplate()).isEqualTo("{\"pipeline\":\"dpdLifecyclePipe\",\"version\":\"1.0.0\"}");
        assertThat(readTaskRes.getConfigurations()).isEqualTo("{\"stage\":\"PROD\"}");
        assertThat(readTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(readTaskRes.getResults()).isNull();
        assertThat(readTaskRes.getErrors()).isNull();
        assertThat(readTaskRes.getCreatedAt()).isNotNull();
        assertThat(readTaskRes.getStartedAt()).isNotNull();
        assertThat(readTaskRes.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTaskAfterStop() {

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

        ActivityTaskResource readTaskRes = devOpsClient.readTask(stoppedTaskRes.getId());
        assertThat(readTaskRes).isNotNull();
        assertThat(readTaskRes.getId()).isNotNull();
        assertThat(readTaskRes.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(readTaskRes.getTemplate()).isEqualTo("{\"pipeline\":\"dpdLifecyclePipe\",\"version\":\"1.0.0\"}");
        assertThat(readTaskRes.getConfigurations()).isEqualTo("{\"stage\":\"PROD\"}");
        assertThat(readTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSED);
        assertThat(readTaskRes.getResults()).isEqualTo("OK");
        assertThat(readTaskRes.getErrors()).isNull();
        assertThat(readTaskRes.getCreatedAt()).isNotNull();
        assertThat(readTaskRes.getStartedAt()).isNotNull();
        assertThat(readTaskRes.getFinishedAt()).isNotNull();

        assertThat(readTaskRes).isEqualTo(stoppedTaskRes);
    }

    // ======================================================================================
    // SEARCH task
    // ======================================================================================
    
    // TODO create multiple activities to be sure that the search call properly filters results
    
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchTaskByActivityId() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity1(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);

        ActivityTaskResource searchedTask = taskResources[0];
        assertThat(searchedTask).isNotNull();
        assertThat(searchedTask.getId()).isNotNull();
        assertThat(searchedTask.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(searchedTask.getTemplate()).isEqualTo("{\"pipeline\":\"dpdLifecyclePipe\",\"version\":\"1.0.0\"}");
        assertThat(searchedTask.getConfigurations()).isEqualTo("{\"stage\":\"PROD\"}");
        assertThat(searchedTask.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(searchedTask.getResults()).isNull();
        assertThat(searchedTask.getErrors()).isNull();
        assertThat(searchedTask.getCreatedAt()).isNotNull();
        assertThat(searchedTask.getStartedAt()).isNotNull();
        assertThat(searchedTask.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchTaskByStatus() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity1(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(null, null, ActivityTaskStatus.PROCESSING);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);

        ActivityTaskResource searchedTask = taskResources[0];
        assertThat(searchedTask).isNotNull();
        assertThat(searchedTask.getId()).isNotNull();
        assertThat(searchedTask.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(searchedTask.getTemplate()).isEqualTo("{\"pipeline\":\"dpdLifecyclePipe\",\"version\":\"1.0.0\"}");
        assertThat(searchedTask.getConfigurations()).isEqualTo("{\"stage\":\"PROD\"}");
        assertThat(searchedTask.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(searchedTask.getResults()).isNull();
        assertThat(searchedTask.getErrors()).isNull();
        assertThat(searchedTask.getCreatedAt()).isNotNull();
        assertThat(searchedTask.getStartedAt()).isNotNull();
        assertThat(searchedTask.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchTaskByExecutor() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity1(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(null, "azure-devops", null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);

        ActivityTaskResource searchedTask = taskResources[0];
        assertThat(searchedTask).isNotNull();
        assertThat(searchedTask.getId()).isNotNull();
        assertThat(searchedTask.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(searchedTask.getTemplate()).isEqualTo("{\"pipeline\":\"dpdLifecyclePipe\",\"version\":\"1.0.0\"}");
        assertThat(searchedTask.getConfigurations()).isEqualTo("{\"stage\":\"PROD\"}");
        assertThat(searchedTask.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(searchedTask.getResults()).isNull();
        assertThat(searchedTask.getErrors()).isNull();
        assertThat(searchedTask.getCreatedAt()).isNotNull();
        assertThat(searchedTask.getStartedAt()).isNotNull();
        assertThat(searchedTask.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchTaskByAll() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity1(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), "azure-devops",
                ActivityTaskStatus.PROCESSING);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);

        ActivityTaskResource searchedTask = taskResources[0];
        assertThat(searchedTask).isNotNull();
        assertThat(searchedTask.getId()).isNotNull();
        assertThat(searchedTask.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(searchedTask.getTemplate()).isEqualTo("{\"pipeline\":\"dpdLifecyclePipe\",\"version\":\"1.0.0\"}");
        assertThat(searchedTask.getConfigurations()).isEqualTo("{\"stage\":\"PROD\"}");
        assertThat(searchedTask.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(searchedTask.getResults()).isNull();
        assertThat(searchedTask.getErrors()).isNull();
        assertThat(searchedTask.getCreatedAt()).isNotNull();
        assertThat(searchedTask.getStartedAt()).isNotNull();
        assertThat(searchedTask.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchTaskByNothing() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity1(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(null, null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);

        ActivityTaskResource searchedTask = taskResources[0];
        assertThat(searchedTask).isNotNull();
        assertThat(searchedTask.getId()).isNotNull();
        assertThat(searchedTask.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(searchedTask.getTemplate()).isEqualTo("{\"pipeline\":\"dpdLifecyclePipe\",\"version\":\"1.0.0\"}");
        assertThat(searchedTask.getConfigurations()).isEqualTo("{\"stage\":\"PROD\"}");
        assertThat(searchedTask.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(searchedTask.getResults()).isNull();
        assertThat(searchedTask.getErrors()).isNull();
        assertThat(searchedTask.getCreatedAt()).isNotNull();
        assertThat(searchedTask.getStartedAt()).isNotNull();
        assertThat(searchedTask.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testSearchTaskMissing() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity1(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(234L, "xxx", ActivityTaskStatus.PLANNED);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(0);
    }
}