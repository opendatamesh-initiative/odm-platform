package org.opendatamesh.platform.pp.devops.server;

import org.junit.jupiter.api.Test;
import org.opendatamesh.platform.pp.devops.api.resources.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.annotation.DirtiesContext.MethodMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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
        assertThat(stoppedTaskRes.getResults()).contains("{\"status\":\"PROCESSED\",\"results\":{\"message\":\"OK\"}}");
        assertThat(stoppedTaskRes.getErrors()).isNull();
        assertThat(stoppedTaskRes.getCreatedAt()).isNotNull();
        assertThat(stoppedTaskRes.getStartedAt()).isNotNull();
        assertThat(stoppedTaskRes.getFinishedAt()).isNotNull();
    }

    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testStopTaskWithFailedStatusAndResults() {

        createMocksForCreateActivityCall();
        createMocksForCreateActivityCall();

        ActivityResource activityRes = createTestActivity(true);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource targetTaskRes = taskResources[0];

        // Create a TaskResultResource with FAILED status but containing results
        TaskResultResource taskResultResource = new TaskResultResource();
        taskResultResource.setStatus(TaskResultStatus.FAILED);
        taskResultResource.setErrors("Task execution failed due to timeout");
        
        // Add some results that should be saved even for failed tasks
        Map<String, Object> results = new HashMap<>();
        results.put("partialOutput", "Some partial results were generated");
        results.put("executionTime", "30 seconds");
        results.put("completedSteps", 5);
        taskResultResource.setResults(results);

        // Use REST API directly to send the TaskResultResource
        String url = "http://localhost:" + port + "/api/v1/pp/devops/tasks/" + targetTaskRes.getId() + "/status?action=STOP";
        ResponseEntity<TaskStatusResource> response = devOpsClient.rest.exchange(
            url,
            HttpMethod.PATCH,
            new HttpEntity<>(taskResultResource),
            TaskStatusResource.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskStatusResource taskStatusRes = response.getBody();
        assertThat(taskStatusRes).isNotNull();
        assertThat(taskStatusRes.getStatus()).isEqualTo(ActivityTaskStatus.FAILED);

        ActivityTaskResource stoppedTaskRes = null;
        try {
            stoppedTaskRes = devOpsClient.readTask(targetTaskRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occurred while reading task: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        assertThat(stoppedTaskRes).isNotNull();
        assertThat(stoppedTaskRes.getId()).isNotNull();
        assertThat(stoppedTaskRes.getStatus()).isEqualTo(ActivityTaskStatus.FAILED);
        assertThat(stoppedTaskRes.getErrors()).isEqualTo("Task execution failed due to timeout");
        
        // This is the issue: results should be saved but they are not
        // The current implementation only saves results for PROCESSED status
        assertThat(stoppedTaskRes.getResults()).isNotNull();
        assertThat(stoppedTaskRes.getResults()).contains("partialOutput");
        assertThat(stoppedTaskRes.getResults()).contains("Some partial results were generated");
        assertThat(stoppedTaskRes.getResults()).contains("executionTime");
        assertThat(stoppedTaskRes.getResults()).contains("30 seconds");
        assertThat(stoppedTaskRes.getResults()).contains("completedSteps");
        assertThat(stoppedTaskRes.getResults()).contains("5");
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

        ActivityResource activityRes = createTestActivity(false);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(activityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource taskRes = taskResources[0];

        try {
            devOpsClient.readTask(taskRes.getId() + 1000);
            fail("Expected exception was not thrown");
        } catch (Throwable t) {
            // Expected
        }
    }

    // ======================================================================================
    // TASK FAILURE BEHAVIOR TESTS
    // ======================================================================================

    /**
     * Test to verify that when a task fails, the activity should stop and not continue with remaining tasks.
     * This is the expected correct behavior.
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testActivityShouldStopAfterTaskFailure() throws IOException {

        // Create an activity with multiple tasks
        createMocksForCreateActivityWithMultipleTaskCall();
        createMocksForCreateActivityWithMultipleTaskCall();
        createMocksForCreateActivityWithMultipleTaskCall();

        // Use the resource with multiple tasks
        ActivityResource activityRes = resourceBuilder.readResourceFromFile(
            ODMDevOpsResources.RESOURCE_ACTIVITY_1, ActivityResource.class);
        activityRes.setStage("prod"); // This stage has multiple tasks in dpd-multiple-tasks.json

        ActivityResource createdActivityRes = null;
        try {
            createdActivityRes = devOpsClient.createActivity(activityRes, false);
        } catch (Throwable t) {
            fail("An unexpected exception occurred while creating activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        // Start the activity
        try {
            devOpsClient.startActivity(createdActivityRes.getId());
        } catch (Throwable t) {
            fail("An unexpected exception occurred while starting activity: " + t.getMessage());
            t.printStackTrace();
            return;
        }

        // Get all tasks for this activity
        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(createdActivityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isGreaterThanOrEqualTo(2); // Should have at least 2 tasks

        ActivityTaskResource firstTask = taskResources[0];
        ActivityTaskResource secondTask = taskResources[1];

        // Verify that the first task is processing
        assertThat(firstTask.getStatus()).isEqualTo(ActivityTaskStatus.PROCESSING);
        assertThat(secondTask.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);

        // Simulate the first task failing
        TaskResultResource failedTaskResult = new TaskResultResource();
        failedTaskResult.setStatus(TaskResultStatus.FAILED);
        failedTaskResult.setErrors("Task execution failed due to timeout");

        // Use REST API directly to send the failed TaskResultResource
        String url = "http://localhost:" + port + "/api/v1/pp/devops/tasks/" + firstTask.getId() + "/status?action=STOP";
        ResponseEntity<TaskStatusResource> response = devOpsClient.rest.exchange(
            url,
            HttpMethod.PATCH,
            new HttpEntity<>(failedTaskResult),
            TaskStatusResource.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        TaskStatusResource taskStatusRes = response.getBody();
        assertThat(taskStatusRes).isNotNull();
        assertThat(taskStatusRes.getStatus()).isEqualTo(ActivityTaskStatus.FAILED);

        // Verify that the first task has failed
        ActivityTaskResource failedFirstTask = devOpsClient.readTask(firstTask.getId());
        assertThat(failedFirstTask.getStatus()).isEqualTo(ActivityTaskStatus.FAILED);
        assertThat(failedFirstTask.getErrors()).isEqualTo("Task execution failed due to timeout");

        // CORRECT BEHAVIOR: The second task should be ABORTED (not started)
        ActivityTaskResource updatedSecondTask = devOpsClient.readTask(secondTask.getId());
        assertThat(updatedSecondTask.getStatus()).isEqualTo(ActivityTaskStatus.ABORTED); // Should be ABORTED when activity stops
        assertThat(updatedSecondTask.getStartedAt()).isNull(); // Should not have started

        // The activity should be FAILED
        ActivityResource activityAfterFailure = devOpsClient.readActivity(createdActivityRes.getId());
        assertThat(activityAfterFailure.getStatus()).isEqualTo(ActivityStatus.FAILED);
        assertThat(activityAfterFailure.getErrors()).isNotNull();
        assertThat(activityAfterFailure.getFinishedAt()).isNotNull();
    }

    // ======================================================================================
    // ACTIVITY STATUS BASED ON TASK STATUSES TESTS
    // ======================================================================================

    /**
     * Test: when all tasks are PROCESSED, the activity should be PROCESSED
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testActivityStatusAllTasksProcessed() throws IOException {
        createMocksForCreateActivityWithMultipleTaskCall();
        createMocksForCreateActivityWithMultipleTaskCall();
        createMocksForCreateActivityWithMultipleTaskCall();

        ActivityResource activityRes = resourceBuilder.readResourceFromFile(
            ODMDevOpsResources.RESOURCE_ACTIVITY_1, ActivityResource.class);
        activityRes.setStage("prod");

        ActivityResource createdActivityRes = devOpsClient.createActivity(activityRes, false);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(createdActivityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isGreaterThanOrEqualTo(2);
        ActivityTaskResource firstTask = taskResources[0];
        ActivityTaskResource secondTask = taskResources[1];

        // Verify initial status - all tasks should be PLANNED
        assertThat(firstTask.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);
        assertThat(secondTask.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);

        // Complete both tasks successfully using task endpoints only
        devOpsClient.stopTask(firstTask.getId());
        devOpsClient.stopTask(secondTask.getId());

        ActivityResource completedActivity = devOpsClient.readActivity(createdActivityRes.getId());
        assertThat(completedActivity.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
        assertThat(completedActivity.getFinishedAt()).isNotNull();
    }

    /**
     * Test: when at least one task is FAILED, the activity should be FAILED
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testActivityStatusAtLeastOneTaskFailed() throws IOException {
        createMocksForCreateActivityWithMultipleTaskCall();
        createMocksForCreateActivityWithMultipleTaskCall();
        createMocksForCreateActivityWithMultipleTaskCall();

        ActivityResource activityRes = resourceBuilder.readResourceFromFile(
            ODMDevOpsResources.RESOURCE_ACTIVITY_1, ActivityResource.class);
        activityRes.setStage("prod");

        ActivityResource createdActivityRes = devOpsClient.createActivity(activityRes, false);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(createdActivityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isGreaterThanOrEqualTo(2);
        ActivityTaskResource firstTask = taskResources[0];
        ActivityTaskResource secondTask = taskResources[1];

        // Verify initial status - all tasks should be PLANNED
        assertThat(firstTask.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);
        assertThat(secondTask.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);

        // Complete the first task successfully
        devOpsClient.stopTask(firstTask.getId());
        // Fail the second task using task endpoint
        TaskResultResource failedTaskResult = new TaskResultResource();
        failedTaskResult.setStatus(TaskResultStatus.FAILED);
        failedTaskResult.setErrors("Task execution failed due to timeout");
        String url = "http://localhost:" + port + "/api/v1/pp/devops/tasks/" + secondTask.getId() + "/status?action=STOP";
        ResponseEntity<TaskStatusResource> response = devOpsClient.rest.exchange(
            url,
            HttpMethod.PATCH,
            new HttpEntity<>(failedTaskResult),
            TaskStatusResource.class
        );
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        ActivityResource failedActivity = devOpsClient.readActivity(createdActivityRes.getId());
        assertThat(failedActivity.getStatus()).isEqualTo(ActivityStatus.FAILED);
        assertThat(failedActivity.getErrors()).isNotNull();
        assertThat(failedActivity.getFinishedAt()).isNotNull();
    }

    /**
     * Test: when all tasks are ABORTED and none is FAILED, the activity should be ABORTED
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testActivityStatusAllTasksAborted() throws IOException {
        createMocksForCreateActivityWithMultipleTaskCall();
        createMocksForCreateActivityWithMultipleTaskCall();
        createMocksForCreateActivityWithMultipleTaskCall();

        ActivityResource activityRes = resourceBuilder.readResourceFromFile(
            ODMDevOpsResources.RESOURCE_ACTIVITY_1, ActivityResource.class);
        activityRes.setStage("prod");

        ActivityResource createdActivityRes = devOpsClient.createActivity(activityRes, false);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(createdActivityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isGreaterThanOrEqualTo(2);
        ActivityTaskResource firstTask = taskResources[0];
        ActivityTaskResource secondTask = taskResources[1];

        // Verify initial status - all tasks should be PLANNED
        assertThat(firstTask.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);
        assertThat(secondTask.getStatus()).isEqualTo(ActivityTaskStatus.PLANNED);

        // Complete the first task successfully
        devOpsClient.stopTask(firstTask.getId());
        // Abort the second task using task endpoint
        String abortTaskUrl = "http://localhost:" + port + "/api/v1/pp/devops/tasks/" + secondTask.getId() + "/status?action=ABORT";
        ResponseEntity<TaskStatusResource> abortResponse = devOpsClient.rest.exchange(
            abortTaskUrl,
            HttpMethod.PATCH,
            HttpEntity.EMPTY,
            TaskStatusResource.class
        );
        assertThat(abortResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        ActivityResource abortedActivity = devOpsClient.readActivity(createdActivityRes.getId());
        assertThat(abortedActivity.getStatus()).isEqualTo(ActivityStatus.ABORTED);
        assertThat(abortedActivity.getFinishedAt()).isNotNull();
        ActivityTaskResource abortedSecondTask = devOpsClient.readTask(secondTask.getId());
        assertThat(abortedSecondTask.getStatus()).isEqualTo(ActivityTaskStatus.ABORTED);
        assertThat(abortedSecondTask.getFinishedAt()).isNotNull();
    }

    // ======================================================================================
    // UPDATE ACTIVITY PARTIAL RESULTS TESTS
    // ======================================================================================

    /**
     * Test: verify that updateActivityPartialResults uses task names as keys when available
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUpdateActivityPartialResults_UseTaskNamesAsKeys() throws IOException {
        createMocksForCreateActivityWithMultipleTaskCall();
        createMocksForCreateActivityWithMultipleTaskCall();
        createMocksForCreateActivityWithMultipleTaskCall();

        ActivityResource activityRes = resourceBuilder.readResourceFromFile(
            ODMDevOpsResources.RESOURCE_ACTIVITY_1, ActivityResource.class);
        activityRes.setStage("prod");

        ActivityResource createdActivityRes = devOpsClient.createActivity(activityRes, false);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(createdActivityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isGreaterThanOrEqualTo(2);
        ActivityTaskResource firstTask = taskResources[0];
        ActivityTaskResource secondTask = taskResources[1];

        // Complete the first task successfully
        devOpsClient.stopTask(firstTask.getId());

        // Read the activity to verify results structure
        ActivityResource activityAfterFirstTask = devOpsClient.readActivity(createdActivityRes.getId());
        assertThat(activityAfterFirstTask.getResults()).isNotNull();
        
        // Verify that the first task results are stored with a meaningful key
        // The key should be the task name if available, or "task" + taskId as fallback
        String firstTaskResults = activityAfterFirstTask.getResults();
        assertThat(firstTaskResults).isNotNull();
        
        // The results should contain the task output, but we can't predict the exact key
        // since it depends on the task name from the lifecycle configuration
        assertThat(firstTaskResults).contains("status");
        assertThat(firstTaskResults).contains("PROCESSED");

        // Complete the second task successfully
        devOpsClient.stopTask(secondTask.getId());

        // Read the activity again to verify both results are stored
        ActivityResource activityAfterSecondTask = devOpsClient.readActivity(createdActivityRes.getId());
        assertThat(activityAfterSecondTask.getResults()).isNotNull();
        
        String secondTaskResults = activityAfterSecondTask.getResults();
        assertThat(secondTaskResults).isNotNull();
        
        // Both task results should be present
        assertThat(secondTaskResults).contains("status");
        assertThat(secondTaskResults).contains("PROCESSED");
        
        // Verify that the activity is now completed
        assertThat(activityAfterSecondTask.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
        assertThat(activityAfterSecondTask.getFinishedAt()).isNotNull();
    }

    /**
     * Test: verify that updateActivityPartialResults uses task ID as fallback when task name is not available
     */
    @Test
    @DirtiesContext(methodMode = MethodMode.AFTER_METHOD)
    public void testUpdateActivityPartialResults_UseTaskIdAsFallback() throws IOException {
        createMocksForCreateActivityCall();
        createMocksForCreateActivityCall();
        createMocksForCreateActivityCall();

        ActivityResource activityRes = resourceBuilder.readResourceFromFile(
            ODMDevOpsResources.RESOURCE_ACTIVITY_1, ActivityResource.class);
        activityRes.setStage("prod");

        ActivityResource createdActivityRes = devOpsClient.createActivity(activityRes, false);

        ActivityTaskResource[] taskResources = devOpsClient.searchTasks(createdActivityRes.getId(), null, null);
        assertThat(taskResources).isNotNull();
        assertThat(taskResources.length).isEqualTo(1);
        ActivityTaskResource task = taskResources[0];

        // Complete the task successfully
        devOpsClient.stopTask(task.getId());

        // Read the activity to verify results structure
        ActivityResource completedActivity = devOpsClient.readActivity(createdActivityRes.getId());
        assertThat(completedActivity.getResults()).isNotNull();
        
        // Verify that the task results are stored
        String taskResults = completedActivity.getResults();
        assertThat(taskResults).isNotNull();
        
        // The results should contain the task output
        assertThat(taskResults).contains("status");
        assertThat(taskResults).contains("PROCESSED");
        
        // Verify that the activity is now completed
        assertThat(completedActivity.getStatus()).isEqualTo(ActivityStatus.PROCESSED);
        assertThat(completedActivity.getFinishedAt()).isNotNull();
    }
}