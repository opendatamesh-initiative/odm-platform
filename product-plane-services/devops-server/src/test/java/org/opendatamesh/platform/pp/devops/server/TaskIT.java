package org.opendatamesh.platform.pp.devops.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.devops.api.resources.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

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

        ActivityResource activityRes = createTestActivity(false);

        try {
            devOpsClient.startActivity(activityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occurred while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource taskRes = taskResources[0];

        ActivityTaskResource readTaskRes = devOpsClient.readTask(taskRes.getId());
        assertThat(readTaskRes).isNotNull();
        assertThat(readTaskRes.getId()).isNotNull();
        assertThat(readTaskRes.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(readTaskRes.getTemplate()).isEqualTo("{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
        assertThat(readTaskRes.getConfigurations()).isEqualTo("{\"stagesToSkip\":[],\"context\":{\"prod\":{\"status\":\"PROCESSING\"}}}");
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
        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource targetTaskRes = taskResources[0];

        TaskStatusResource taskStatusRes = null;
        try {
            taskStatusRes = devOpsClient.stopTask(targetTaskRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occurred while stopping task: " + t.getMessage());
            t.printStackTrace();
            return;
        }
        assertThat(taskStatusRes).isNotNull();
        assertThat(taskStatusRes.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSED);

        ActivityTaskResource stoppedTaskRes = null;
        try {
            stoppedTaskRes = devOpsClient.readTask(targetTaskRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occurred while stopping task: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(stoppedTaskRes).isNotNull();
        assertThat(stoppedTaskRes.getId()).isNotNull();
        assertThat(stoppedTaskRes.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(stoppedTaskRes.getTemplate()).isEqualTo("{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
        assertThat(stoppedTaskRes.getConfigurations()).isEqualTo("{\"stagesToSkip\":[],\"context\":{\"prod\":{\"status\":\"PROCESSING\"}}}");
        assertThat(stoppedTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSED);
        assertThat(stoppedTaskRes.getResults()).isNotNull();
        assertThat(stoppedTaskRes.getResults()).isEqualTo("{\"status\":\"PROCESSED\",\"results\":{\"message\":\"OK\"}}");
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

        ActivityResource activityRes = createTestActivity(false);

        ActivityTaskResource[] tasks = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(tasks).isNotNull();
        assertThat(tasks.length).isEqualTo(1);
        ActivityTaskResource taskRes = tasks[0];

        TaskStatusResource statusRes = devOpsClient.readTaskStatus(taskRes.getId());
        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTaskStatusAfterCreateAndStart() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity(true);

        ActivityTaskResource[] tasks = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(tasks).isNotNull();
        assertThat(tasks.length).isEqualTo(1);
        ActivityTaskResource taskRes = tasks[0];

        TaskStatusResource statusRes = devOpsClient.readTaskStatus(taskRes.getId());
        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTaskStatusAfterStart() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity(false);

        ActivityStatusResource startedActivityRes = null;
        try {
            startedActivityRes = devOpsClient.startActivity(activityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occurred while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource taskRes = taskResources[0];

        TaskStatusResource statusRes = devOpsClient.readTaskStatus(taskRes.getId());
        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTaskStatusAfterStop() {

        createMocksForCreateActivityCall();
        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource targetTaskRes = taskResources[0];

        try {
            devOpsClient.stopTask(targetTaskRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occurred while stopping task: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        TaskStatusResource statusRes = devOpsClient.readTaskStatus(activityRes.getId());
        assertThat(statusRes).isNotNull();
        assertThat(statusRes.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSED);
    }


    // ======================================================================================
    // READ task
    // ======================================================================================
    
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTasks() {

        createMocksForCreateActivityCall();

        ActivityResource activityRes = null;
        activityRes = createTestActivity(true);

        ActivityTaskResource[] tasks = devOpsClient.readAllTasks();
        assertThat(tasks).isNotNull();
        assertThat(tasks.length).isEqualTo(1);
        ActivityTaskResource task = tasks[0];
        assertThat(task).isNotNull();
        assertThat(task.getId()).isNotNull();
        assertThat(task.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(task.getTemplate()).isEqualTo("{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
        assertThat(task.getConfigurations()).isEqualTo("{\"stagesToSkip\":[],\"context\":{\"prod\":{\"status\":\"PROCESSING\"}}}");
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
        activityRes = createTestActivity(false);

        ActivityTaskResource[] tasks = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(tasks).isNotNull();
        assertThat(tasks.length).isEqualTo(1);
        ActivityTaskResource taskRes = tasks[0];

        ActivityTaskResource readTaskRes = devOpsClient.readTask(taskRes.getId());
        assertThat(readTaskRes).isNotNull();
        assertThat(readTaskRes.getId()).isNotNull();
        assertThat(readTaskRes.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(readTaskRes.getTemplate()).isEqualTo("{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
        assertThat(readTaskRes.getConfigurations()).isEqualTo("{\"stagesToSkip\":[]}");
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
        activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource taskRes = taskResources[0];

        ActivityTaskResource readTaskRes = devOpsClient.readTask(taskRes.getId());
        assertThat(readTaskRes).isNotNull();
        assertThat(readTaskRes.getId()).isNotNull();
        assertThat(readTaskRes.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(readTaskRes.getTemplate()).isEqualTo("{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
        assertThat(readTaskRes.getConfigurations()).isEqualTo("{\"stagesToSkip\":[],\"context\":{\"prod\":{\"status\":\"PROCESSING\"}}}");
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

        ActivityResource activityRes = createTestActivity(false);

        try {
            devOpsClient.startActivity(activityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occurred while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource taskRes = taskResources[0];

        ActivityTaskResource readTaskRes = devOpsClient.readTask(taskRes.getId());
        assertThat(readTaskRes).isNotNull();
        assertThat(readTaskRes.getId()).isNotNull();
        assertThat(readTaskRes.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(readTaskRes.getTemplate()).isEqualTo("{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
        assertThat(readTaskRes.getConfigurations()).isEqualTo("{\"stagesToSkip\":[],\"context\":{\"prod\":{\"status\":\"PROCESSING\"}}}");
        assertThat(readTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(readTaskRes.getResults()).isNull();
        assertThat(readTaskRes.getErrors()).isNull();
        assertThat(readTaskRes.getCreatedAt()).isNotNull();
        assertThat(readTaskRes.getStartedAt()).isNotNull();
        assertThat(readTaskRes.getFinishedAt()).isNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testMultipleTasks() {

        // Simulate the creation of an Activity with multiple Tasks
        createMocksForCreateActivityWithMultipleTaskCall();
        ActivityResource activityRes = createTestActivity(false);

        // Start the Activity
        try {
            devOpsClient.startActivity(activityRes.getId());
        } catch (Throwable t) {
            fail("Unexpected exception while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        // Retrieve the Tasks associated with the Activity
        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(2); // It should have at least 2 Tasks

        ActivityTaskResource firstTask = taskResources[0];
        ActivityTaskResource secondTask = taskResources[1];

        // Verify that the first Task is in PROCESSING state and the second one is in PLANNED state
        assertThat(firstTask.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(secondTask.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);

        // Simulate the callback for the completion of the first Task
        try {
            devOpsClient.stopTask(firstTask.getId());
        } catch (Throwable t) {
            fail("Unexpected exception while stopping first task: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        // Verify that the first Task has been completed
        ActivityTaskResource stoppedFirstTask = devOpsClient.readTask(firstTask.getId());
        assertThat(stoppedFirstTask.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSED);
        assertThat(stoppedFirstTask.getFinishedAt()).isNotNull();

        // Verify that the second Task has been automatically started
        ActivityTaskResource updatedSecondTask = devOpsClient.readTask(secondTask.getId());
        assertThat(updatedSecondTask.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(updatedSecondTask.getStartedAt()).isNotNull();

        // Simulate the callback for the completion of the second Task
        try {
            devOpsClient.stopTask(secondTask.getId());
        } catch (Throwable t) {
            fail("Unexpected exception while stopping second task: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        // Verify that the second Task has been completed
        ActivityTaskResource stoppedSecondTask = devOpsClient.readTask(secondTask.getId());
        assertThat(stoppedSecondTask.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSED);
        assertThat(stoppedSecondTask.getFinishedAt()).isNotNull();

        // The Activity should be completed
        ActivityResource completedActivity = devOpsClient.readActivity(activityRes.getId());
        assertThat(completedActivity.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testReadTaskAfterStop() {

        createMocksForCreateActivityCall();
        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource targetTaskRes = taskResources[0];

        try {
            devOpsClient.stopTask(targetTaskRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occurred while stopping task: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        ActivityTaskResource readTaskRes = devOpsClient.readTask(targetTaskRes.getId());
        assertThat(readTaskRes).isNotNull();
        assertThat(readTaskRes.getId()).isNotNull();
        assertThat(readTaskRes.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(readTaskRes.getTemplate()).isEqualTo("{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
        assertThat(readTaskRes.getConfigurations()).isEqualTo("{\"stagesToSkip\":[],\"context\":{\"prod\":{\"status\":\"PROCESSING\"}}}");
        assertThat(readTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSED);
        assertThat(readTaskRes.getResults()).isEqualTo("{\"status\":\"PROCESSED\",\"results\":{\"message\":\"OK\"}}");
        assertThat(readTaskRes.getErrors()).isNull();
        assertThat(readTaskRes.getCreatedAt()).isNotNull();
        assertThat(readTaskRes.getStartedAt()).isNotNull();
        assertThat(readTaskRes.getFinishedAt()).isNotNull();
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
        activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);

        ActivityTaskResource searchedTask = taskResources[0];
        assertThat(searchedTask).isNotNull();
        assertThat(searchedTask.getId()).isNotNull();
        assertThat(searchedTask.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(searchedTask.getTemplate()).isEqualTo("{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
        assertThat(searchedTask.getConfigurations()).isEqualTo("{\"stagesToSkip\":[],\"context\":{\"prod\":{\"status\":\"PROCESSING\"}}}");
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
        activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(null, null, ActivityTaskStatus.PROCESSING);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);

        ActivityTaskResource searchedTask = taskResources[0];
        assertThat(searchedTask).isNotNull();
        assertThat(searchedTask.getId()).isNotNull();
        assertThat(searchedTask.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(searchedTask.getTemplate()).isEqualTo("{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
        assertThat(searchedTask.getConfigurations()).isEqualTo("{\"stagesToSkip\":[],\"context\":{\"prod\":{\"status\":\"PROCESSING\"}}}");
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
        activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(null, "azure-devops", null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);

        ActivityTaskResource searchedTask = taskResources[0];
        assertThat(searchedTask).isNotNull();
        assertThat(searchedTask.getId()).isNotNull();
        assertThat(searchedTask.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(searchedTask.getTemplate()).isEqualTo("{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
        assertThat(searchedTask.getConfigurations()).isEqualTo("{\"stagesToSkip\":[],\"context\":{\"prod\":{\"status\":\"PROCESSING\"}}}");
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
        activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), "azure-devops",
                ActivityTaskStatus.PROCESSING);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);

        ActivityTaskResource searchedTask = taskResources[0];
        assertThat(searchedTask).isNotNull();
        assertThat(searchedTask.getId()).isNotNull();
        assertThat(searchedTask.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(searchedTask.getTemplate()).isEqualTo("{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
        assertThat(searchedTask.getConfigurations()).isEqualTo("{\"stagesToSkip\":[],\"context\":{\"prod\":{\"status\":\"PROCESSING\"}}}");
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
        activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(null, null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);

        ActivityTaskResource searchedTask = taskResources[0];
        assertThat(searchedTask).isNotNull();
        assertThat(searchedTask.getId()).isNotNull();
        assertThat(searchedTask.getExecutorRef()).isEqualTo("azure-devops");
        assertThat(searchedTask.getTemplate()).isEqualTo("{\"organization\":\"andreagioia\",\"project\":\"opendatamesh\",\"pipelineId\":\"3\",\"branch\":\"main\"}");
        assertThat(searchedTask.getConfigurations()).isEqualTo("{\"stagesToSkip\":[],\"context\":{\"prod\":{\"status\":\"PROCESSING\"}}}");
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
        activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(234L, "xxx", ActivityTaskStatus.PLANNED);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(0);
    }
}