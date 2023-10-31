package org.opendatamesh.platform.pp.devops.server.services;

import java.time.LocalDateTime;
import java.util.*;

import org.opendatamesh.platform.core.commons.servers.exceptions.*;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.core.StandardDefinitionDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.LifecycleTaskInfoDPDS;
import org.opendatamesh.platform.core.dpds.parser.IdentifierStrategy;
import org.opendatamesh.platform.pp.devops.api.clients.DevOpsAPIRoutes;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.opendatamesh.platform.pp.devops.api.resources.DevOpsApiStandardErrors;
import org.opendatamesh.platform.pp.devops.api.resources.TaskResultResource;
import org.opendatamesh.platform.pp.devops.api.resources.TaskResultStatus;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsClients;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsConfigurations;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.opendatamesh.platform.pp.devops.server.database.mappers.TaskMapper;
import org.opendatamesh.platform.pp.devops.server.database.repositories.TaskRepository;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
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
    public List<Task> createTasks(Long activityId, List<LifecycleTaskInfoDPDS> activitiesInfo) {

        List<Task> tasks = new ArrayList<Task>();

        for (LifecycleTaskInfoDPDS activityInfo : activitiesInfo) {
            Task task = buildTask(activityId, activityInfo);
            task = createTask(task);
            tasks.add(task);
        }

        return tasks;
    }

    public Task createTask(Task task) {

        if (task == null) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                "Task object cannot be null");
        }

        // TODO (?) control if task already exists
        task.setStatus(ActivityTaskStatus.PLANNED);
        
        try {
            task = saveTask(task);
            logger.info("Task [" + task.getId() + "] succesfully created");
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
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
            task.setStartedAt(now());
            saveTask(task);

            if(task.getExecutorRef() != null) {
                task = submitTask(task);
                if (task.getStatus().equals(ActivityTaskStatus.FAILED)) {
                    saveTask(task);
                }
            } else {
                Map<String, Object> results = new HashMap<>();
                results.put("message", "Nothing to do. Task succeded by default");
                task.setResults(results);
                task.setStatus(ActivityTaskStatus.PROCESSED);
                task.setFinishedAt(now());
            }
            
        } catch(Throwable t) {
             throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
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
            callbackRef += "/" + task.getId() + "/status?action=STOP";
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
            task.setFinishedAt(now());
        }
       
        return task;
    }

    public Task stopTask(Long taskId, TaskResultResource taskResultResource) {
        Task task = readTask(taskId);
        return stopTask(task, taskResultResource);
    }

    public Task stopTask(Task task, TaskResultResource taskResultResource) {
		try {
            if(!(taskResultResource == null)) {
                if(taskResultResource.getStatus() == null)
                    throw new UnprocessableEntityException(
                            DevOpsApiStandardErrors.SC422_03_TASK_RESULT_IS_INVALID,
                            "Task status cannot be null"
                    );
                if(taskResultResource.getStatus().equals(TaskResultStatus.PROCESSED)) {
                    task.setStatus(ActivityTaskStatus.PROCESSED);
                    task.setResults(taskResultResource.getResults());
                }
                else {
                    task.setStatus(ActivityTaskStatus.FAILED);
                    task.setErrors(taskResultResource.getErrors());
                }
            } else {
                task.setStatus(ActivityTaskStatus.PROCESSED);
                Map<String, Object> results = new HashMap<>();
                results.put("message", "OK");
                task.setResults(results);
            }
            task.setFinishedAt(now());
            saveTask(task);
        } catch(Throwable t) {
             throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
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
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
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
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Task object cannot be null");
        }
        return readTask(task.getId());
    }

    public Task readTask(Long taskId) {

        Task task = null;

        if (taskId == null) {
            throw new BadRequestException(
                    DevOpsApiStandardErrors.SC400_60_TASK_ID_IS_EMPTY,
                    "Task id is empty");
        }

        try {
            task = loadTask(taskId);
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading task with id [" + taskId
                            + "]",
                    t);
        }

        if (task == null) {
            throw new NotFoundException(
                    DevOpsApiStandardErrors.SC404_11_TASK_NOT_FOUND,
                    "Task with [" + taskId + "] does not exist");
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
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
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
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
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

    private Task buildTask(Long activityId, LifecycleTaskInfoDPDS activityInfo) {
        Task task = null;

        task = new Task();
        task.setActivityId(activityId);
        String executorServiceRef = activityInfo.getService() != null? activityInfo.getService().getHref(): null;
        task.setExecutorRef(executorServiceRef);

        if (activityInfo.hasTemplate()) {
            ExternalComponentResource template = readTemplateDefinition(activityInfo.getTemplate());
            task.setTemplate(template.getDefinition());
        }
        if (activityInfo.hasConfigurations()) {
            String configurationsString = serializeCongigurations(activityInfo.getConfigurations());
            task.setConfigurations(configurationsString);
        }

        return task;
    }

    private ExternalComponentResource readTemplateDefinition(StandardDefinitionDPDS template) {
        ExternalComponentResource templateDefinition = null;

        Objects.requireNonNull(template, "Template parameter cannot be null");
        Objects.requireNonNull(template.getDefinition(), "Property [definition] in template object cannot be null");
        Objects.requireNonNull(template.getDefinition().getRef(),
                "Property [$ref] in template definition object cannot be null");

      
        String templateId = IdentifierStrategy.DEFUALT.getId(
             template.getFullyQualifiedName());

        try {
            templateDefinition = clients.getRegistryClient().readTemplate(templateId);
            logger.debug("Template definition [" + templateId + "] succesfully read from ODM Registry");
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "An error occured in the backend service while loading template [" + templateId + "]",
                    t);
        }
        if (templateDefinition == null) {
            throw new NotFoundException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
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
                ODMApiCommonErrors.SC500_02_DESCRIPTOR_ERROR,
                    "An error occured in the backend service while parsing configurations [" + configurations + "]",
                    t);
        }

        return serializedConfigurations;
    }

    private LocalDateTime now() {
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 
        now.getHour(), now.getMinute(), now.getSecond(), 0);
        return now;
    }

	
}
