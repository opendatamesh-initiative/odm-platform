package org.opendatamesh.platform.pp.devops.server.controllers;

import org.opendatamesh.platform.pp.devops.api.controllers.AbstractTaskController;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.opendatamesh.platform.pp.devops.api.resources.TaskResultResource;
import org.opendatamesh.platform.pp.devops.api.resources.TaskStatusResource;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.opendatamesh.platform.pp.devops.server.database.mappers.ActivityTaskMapper;
import org.opendatamesh.platform.pp.devops.server.services.ActivityService;
import org.opendatamesh.platform.pp.devops.server.services.TaskService;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class TaskController extends AbstractTaskController {

	@Autowired
	ActivityService activityService;
    
	@Autowired
    TaskService taskService;

    @Autowired
    ActivityTaskMapper activityTaskMapper;

	@Override
	public List<ActivityTaskResource> readTasks(
		Long activityId,
        String executorRef, 
        ActivityTaskStatus status
	) {
		List<Task> tasks = null;

		if(activityId != null || executorRef != null || status != null) {
			tasks = taskService.searchTasks(activityId, executorRef, status);
		} else {
			tasks = taskService.readAllTasks();
		}

        return activityTaskMapper.toResources(tasks);
	}

	@Override
	public ActivityTaskResource readTask(Long id) {
		Task task = taskService.readTask(id);
        return activityTaskMapper.toResource(task);
	}

	@Override
	public TaskStatusResource startTask(Long id, Map<String, String> headers) {
		// Get the task to retrieve its activity ID
		Task task = taskService.readTask(id);
		
		// Process executor secrets and store them in cache
		DevOpsClients.extractAndStoreExecutorSecrets(headers, task.getActivityId());
		
		// Start the task
		task = taskService.startSingleTask(id);
		TaskStatusResource statusRes = new TaskStatusResource();
		statusRes.setStatus(task.getStatus());
		
		// Clean up secrets cache
		DevOpsClients.removeAllSecretsForActivity(task.getActivityId());
		
		return statusRes;
    }

	@Override
	public TaskStatusResource abortTask(Long id) {
		Task task = taskService.abortTask(id);
		
		// Update activity status based on all task statuses
		activityService.updateActivityStatusBasedOnTaskStatuses(task.getActivityId());
		
		TaskStatusResource statusRes = new TaskStatusResource();
		statusRes.setStatus(task.getStatus());
		return statusRes;
    }

	@Override
	public TaskStatusResource readTaskStatus(Long id) {
		Task task = taskService.readTask(id);
		TaskStatusResource statusRes = new TaskStatusResource();
		statusRes.setStatus(task.getStatus());
		return statusRes;
	}

    @Override
	public TaskStatusResource stopTask(Long id, TaskResultResource taskResultResource, Boolean updateVariables) {
		Task task = activityService.stopTaskAndUpdateParentActivity(id, taskResultResource, updateVariables);
        TaskStatusResource statusRes = new TaskStatusResource();
		statusRes.setStatus(task.getStatus());
		return statusRes;
	}
}
