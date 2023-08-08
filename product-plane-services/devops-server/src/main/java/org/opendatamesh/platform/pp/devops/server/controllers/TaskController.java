package org.opendatamesh.platform.pp.devops.server.controllers;

import java.util.List;

import org.opendatamesh.platform.pp.devops.api.controllers.AbstractTaskController;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskResource;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.opendatamesh.platform.pp.devops.server.database.mappers.ActivityTaskMapper;
import org.opendatamesh.platform.pp.devops.server.services.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController extends AbstractTaskController {

    @Autowired
    TaskService taskService;

    @Autowired
    ActivityTaskMapper activityTaskMapper;

	@Override
	public List<ActivityTaskResource> readTasks() {
		List<Task> tasks = taskService.readAllTasks();
        return activityTaskMapper.toResources(tasks);
	}

	@Override
	public ActivityTaskResource readTask(Long id) {
		Task task = taskService.readTask(id);
        return activityTaskMapper.toResource(task);
	}

    @Override
	public String readTaskStatus(Long id) {
		Task task = taskService.readTask(id);
		return task.getStatus().toString();
	}

    @Override
	public ActivityTaskResource stopTask(Long id) {
		Task task = taskService.stopTask(id);
        return activityTaskMapper.toResource(task);
	}
    
}
