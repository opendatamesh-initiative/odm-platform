package org.opendatamesh.platform.pp.devops.server.controllers;

import java.util.List;

import org.opendatamesh.platform.pp.devops.api.controllers.AbstractTaskController;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.opendatamesh.platform.pp.devops.api.resources.TaskResultResource;
import org.opendatamesh.platform.pp.devops.api.resources.TaskStatusResource;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.opendatamesh.platform.pp.devops.server.database.mappers.ActivityTaskMapper;
import org.opendatamesh.platform.pp.devops.server.services.ActivityService;
import org.opendatamesh.platform.pp.devops.server.services.TaskService;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

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
