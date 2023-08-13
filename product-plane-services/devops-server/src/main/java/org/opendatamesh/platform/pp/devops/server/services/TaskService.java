package org.opendatamesh.platform.pp.devops.server.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsAPIRoutes;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ODMDevOpsAPIStandardError;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsClients;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsConfigurations;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.opendatamesh.platform.pp.devops.server.database.mappers.TaskMapper;
import org.opendatamesh.platform.pp.devops.server.database.repositories.TaskRepository;
import org.opendatamesh.platform.pp.devops.server.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.devops.server.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.devops.server.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DefinitionResource;
import org.opendatamesh.platform.up.executor.api.clients.ExecutorClient;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class TaskService {
    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskMapper taskMapper;

    @Autowired
    DevOpsConfigurations configurations;

    @Autowired
    DevOpsClients clients;

    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    public TaskService() {

    }

    // ======================================================================================
    // CREATE
    // ======================================================================================

    // Create tasks and start activity
    public List<Task> createTasks(Long activityId, List<LifecycleActivityInfoDPDS> activitiesInfo) {

        List<Task> tasks = new ArrayList<Task>();

        for (LifecycleActivityInfoDPDS activityInfo : activitiesInfo) {
            Task task = buildTask(activityId, activityInfo);
            task = createTask(task);
            tasks.add(task);
        }

        return tasks;
    }

    public Task createTask(Task task) {

        if (task == null) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Task object cannot be null");
        }

        // TODO (?) control if task already exists
        task.setStatus(ActivityTaskStatus.PLANNED);
        
        try {
            task = saveTask(task);
            logger.info("Task [" + task.getId() + "] succesfully created");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving task",
                    t);
        }

        return task;
    }

    public Task saveTask(Task task) {
        return taskRepository.saveAndFlush(task);
    }    

    // ======================================================================================
    // START/STOP
    // ======================================================================================

    public Task startTask(Long taskId) {
        Task task = readTask(taskId);
        return startTask(task);
    }
    
    public Task startTask(Task task) {

        try {
            task.setStatus(ActivityTaskStatus.PROCESSING);
            task.setStartedAt(new Date());
            saveTask(task);

            if(task.getExecutorRef() != null) {
                task = submitTask(task);
                if (task.getStatus().equals(ActivityTaskStatus.FAILED)) {
                    saveTask(task);
                }
            } else {
                task.setResults("Nothing to do. Task succeded by default");
                task.setStatus(ActivityTaskStatus.PROCESSED);
                task.setFinishedAt(new Date());
            }
            
        } catch(Throwable t) {
             throw new InternalServerException(
                ODMDevOpsAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while saving task",
                t);
        }
        
        return task;
    }

    private Task submitTask(Task task) {
    
        try {
            TaskResource taskRes = taskMapper.toResource(task);
            String callbackRef = configurations.getProductPlane().getDevopsService().getAddress();
            callbackRef += DevOpsAPIRoutes.TASKS;
            callbackRef += "/" + task.getId() + "/stop";
            taskRes.setCallbackRef(callbackRef);

            ExecutorClient odmExecutor = clients.getExecutorClient(task.getExecutorRef());
            if (odmExecutor != null) {
                taskRes = odmExecutor.createTask(taskRes);
            } else {
                taskRes.setStatus(TaskStatus.FAILED);
                taskRes.setErrors("Executor [" + task.getExecutorRef() + "] supported");
                taskRes.setFinishedAt(new Date());
            }

            task = taskMapper.toEntity(taskRes);
        } catch(Throwable t) {
            task.setStatus(ActivityTaskStatus.FAILED);
            task.setErrors(t.getMessage());
            task.setFinishedAt(new Date());
        }
       
        return task;
    }

    public Task stopTask(Long taskId) {
        Task task = readTask(taskId);
        return stopTask(task);
    }

    public Task stopTask(Task task) {
		try {
            task.setStatus(ActivityTaskStatus.PROCESSED);
            task.setFinishedAt(new Date());
            task.setResults("OK");
            saveTask(task);
        } catch(Throwable t) {
             throw new InternalServerException(
                ODMDevOpsAPIStandardError.SC500_01_DATABASE_ERROR,
                "An error occured in the backend database while saving task",
                t);
        }
        
        return task;
	}


    // ======================================================================================
    // READ
    // ======================================================================================

    public List<Task> readAllTasks() {
        List<Task> tasks = null;
        try {
            tasks = loadAllTasks();
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading tasks",
                    t);
        }
        return tasks;
    }

    private List<Task> loadAllTasks() {
        return taskRepository.findAll();
    }

    public Task readTask(Task task) {
        if (task == null) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Task object cannot be null");
        }
        return readTask(task.getId());
    }

    public Task readTask(Long taskId) {

        Task task = null;

        if (taskId == null) {
            throw new BadRequestException(
                    ODMDevOpsAPIStandardError.SC400_50_ACTIVITY_ID_IS_EMPTY,
                    "Task id is empty");
        }

        try {
            task = loadTask(taskId);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading task with id [" + taskId
                            + "]",
                    t);
        }

        if (task == null) {
            throw new NotFoundException(
                    ODMDevOpsAPIStandardError.SC404_01_ACTIVITY_NOT_FOUND,
                    "Data Product with [" + taskId + "] does not exist");
        }

        return task;
    }

    private Task loadTask(Long tasktId) {
        Task task = null;

        Optional<Task> taskLookUpResults = taskRepository.findById(tasktId);

        if (taskLookUpResults.isPresent()) {
            task = taskLookUpResults.get();
        }

        return task;
    }

    // -------------------------
    // exists methods
    // -------------------------

    private boolean taskExists(Long taskId) {
        if (taskId == null) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Task object cannot be null");
        }

        return taskRepository.existsById(taskId);
    }

    public boolean activityExists(Long taskId) {
        return taskRepository.existsById(taskId);
    }

    // -------------------------
    // search methods
    // -------------------------
    public List<Task> searchTasks(
        Long activityId,
        String executorRef, 
        ActivityTaskStatus status) 
    {
        List<Task> taskSearchResults = null;
        try {
            taskSearchResults = findTasks(activityId, executorRef, status);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching tasks",
                    t);
        }
        return taskSearchResults;
    }

    private List<Task> findTasks(
        Long activityId,
        String executorRef, 
        ActivityTaskStatus status) 
    {
        return taskRepository
            .findAll(TaskRepository.Specs.hasMatch(
                activityId, executorRef, status));
    }

    // -------------------------
    // other methods
    // -------------------------

    private Task buildTask(Long activityId, LifecycleActivityInfoDPDS activityInfo) {
        Task task = null;

        task = new Task();
        task.setActivityId(activityId);
        String executorServiceRef = activityInfo.getService() != null? activityInfo.getService().getHref(): null;
        task.setExecutorRef(executorServiceRef);

        if (activityInfo.hasTemplate()) {
            DefinitionResource templateDefinition = readTemplateDefinition(activityInfo.getTemplate());
            task.setTemplate(templateDefinition.getContent());
        }
        if (activityInfo.hasConfigurations()) {
            String configurationsString = serializeCongigurations(activityInfo.getConfigurations());
            task.setConfigurations(configurationsString);
        }

        return task;
    }

    private DefinitionResource readTemplateDefinition(StandardDefinitionDPDS template) {
        DefinitionResource templateDefinition = null;

        Objects.requireNonNull(template, "Template parameter cannot be null");
        Objects.requireNonNull(template.getDefinition(), "Property [definition] in template object cannot be null");
        Objects.requireNonNull(template.getDefinition().getRef(),
                "Property [$ref] in template definition object cannot be null");

        String ref = template.getDefinition().getRef();

        Long templateId = null;
        try {
            templateId = Long.parseLong(ref.substring(ref.lastIndexOf('/') + 1));
            templateDefinition = clients.getRegistryClient().readOneTemplateDefinition(templateId);
            logger.debug("Template definition [" + templateId + "] succesfully read from ODM Registry");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_00_SERVICE_ERROR,
                    "An error occured in the backend service while loading template [" + templateId + "]",
                    t);
        }
        if (templateDefinition == null) {
            throw new NotFoundException(
                    ODMDevOpsAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Template with id [" + templateId + "] does not existe");
        }

        return templateDefinition;
    }

    private String serializeCongigurations(Map<String, Object> configurations) {
        String serializedConfigurations = null;

        Objects.requireNonNull(configurations, "Configurations parameter cannot be null");

        try {
            serializedConfigurations = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(configurations);
        } catch (JsonProcessingException t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_02_DESCRIPTOR_ERROR,
                    "An error occured in the backend service while parsing configurations [" + configurations + "]",
                    t);
        }

        return serializedConfigurations;
    }

	
}
