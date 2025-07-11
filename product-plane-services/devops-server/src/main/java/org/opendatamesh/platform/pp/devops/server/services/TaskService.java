package org.opendatamesh.platform.pp.devops.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.opendatamesh.dpds.model.core.StandardDefinitionDPDS;
import org.opendatamesh.dpds.model.internals.LifecycleTaskInfoDPDS;
import org.opendatamesh.dpds.parser.IdentifierStrategy;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;
import org.opendatamesh.platform.core.commons.servers.exceptions.*;
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
import org.opendatamesh.platform.pp.devops.server.services.proxies.DevOpsNotificationServiceProxy;
import org.opendatamesh.platform.pp.devops.server.services.proxies.DevopsPolicyServiceProxy;
import org.opendatamesh.platform.pp.registry.api.resources.ExternalComponentResource;
import org.opendatamesh.platform.up.executor.api.clients.ExecutorClient;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class TaskService {

    @Autowired
    private DevopsPolicyServiceProxy policyServiceProxy;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private DevOpsConfigurations configurations;

    @Autowired
    private DevOpsClients clients;

    @Autowired
    private DevOpsNotificationServiceProxy devOpsNotificationServiceProxy;

    @Autowired
    private IdentifierStrategy identifierStrategy;

    @Autowired
    @Lazy
    private ActivityService activityService;

    private ExecutorClient odmExecutor;

    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);


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
            logger.info("Task [" + task.getId() + "] successfully created");
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occurred in the backend database while saving task",
                    t);
        }

        devOpsNotificationServiceProxy.notifyTaskCreation(taskMapper.toResource(task));

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
    
    public Task startSingleTask(Long taskId) {

        Task task = readTask(taskId);
        task.setStartedByActivity(false);
        try {
            task.setStatus(ActivityTaskStatus.PROCESSING);
            task.setStartedAt(now());
            saveTask(task);

            devOpsNotificationServiceProxy.notifyTaskStart(taskMapper.toResource(task));

            if(task.getExecutorRef() != null) {
                task = submitTask(task);
                if (task.getStatus().equals(ActivityTaskStatus.FAILED)) {
                    task = saveTask(task);
                    devOpsNotificationServiceProxy.notifyTaskCompletion(taskMapper.toResource(task));
                    // Update activity status when task fails
                    activityService.updateActivityStatusBasedOnTaskStatuses(task.getActivityId());
                }
            } else {
                TaskResultResource taskResultResource = new TaskResultResource();
                taskResultResource.setStatus(TaskResultStatus.PROCESSED);
                Map<String, Object> results = new HashMap<>();
                results.put("message", "Nothing to do. Task succeded by default");
                task.setResults(taskResultResource.toJsonString());
                task.setStatus(ActivityTaskStatus.PROCESSED);
                task.setFinishedAt(now());
                devOpsNotificationServiceProxy.notifyTaskCompletion(taskMapper.toResource(task));
                // Update activity status when task completes successfully
                activityService.updateActivityStatusBasedOnTaskStatuses(task.getActivityId());
            }
            
        } catch(Throwable t) {
             throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occurred in the backend database while saving task",
                t
             );
        }
        
        return task;
    }

    public Task startTask(Task task) {
        try {

            task.setStatus(ActivityTaskStatus.PROCESSING);
            task.setStartedAt(now());
            task.setStartedByActivity(true);
            saveTask(task);

            devOpsNotificationServiceProxy.notifyTaskStart(taskMapper.toResource(task));

            if(task.getExecutorRef() != null) {
                task = submitTask(task);
                if (task.getStatus().equals(ActivityTaskStatus.FAILED)) {
                    task = saveTask(task);
                    devOpsNotificationServiceProxy.notifyTaskCompletion(taskMapper.toResource(task));
                    // Update activity status when task fails
                    activityService.updateActivityStatusBasedOnTaskStatuses(task.getActivityId());
                }
            } else {
                TaskResultResource taskResultResource = new TaskResultResource();
                taskResultResource.setStatus(TaskResultStatus.PROCESSED);
                Map<String, Object> results = new HashMap<>();
                results.put("message", "Nothing to do. Task succeded by default");
                task.setResults(taskResultResource.toJsonString());
                task.setStatus(ActivityTaskStatus.PROCESSED);
                task.setFinishedAt(now());
                devOpsNotificationServiceProxy.notifyTaskCompletion(taskMapper.toResource(task));
                // Update activity status when task completes successfully
                activityService.updateActivityStatusBasedOnTaskStatuses(task.getActivityId());
            }
            
        } catch(Throwable t) {
             throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occurred in the backend database while saving task",
                t
             );
        }
        
        return task;
    }

    private Task submitTask(Task task) {
    
        try {
            TaskResource taskRes = taskMapper.toResource(task);
            String callbackRef = configurations.getProductPlane().getDevopsService().getAddress();
            callbackRef += DevOpsAPIRoutes.TASKS.getPath();
            callbackRef += "/" + task.getId() + "/status?action=STOP";
            taskRes.setCallbackRef(callbackRef);

            odmExecutor = clients.getExecutorClient(task.getExecutorRef());
            if (odmExecutor != null) {
                taskRes = odmExecutor.createTask(taskRes);
            } else {
                taskRes.setStatus(TaskStatus.FAILED);
                taskRes.setErrors("Executor [" + task.getExecutorRef() + "] not supported"); // CHECK
                taskRes.setFinishedAt(new Date());
                devOpsNotificationServiceProxy.notifyTaskCompletion(taskRes);
            }

            task = taskMapper.toEntity(taskRes);
        } catch(Throwable t) {
            task.setStatus(ActivityTaskStatus.FAILED);
            task.setErrors(t.getMessage());
            task.setFinishedAt(now());
            devOpsNotificationServiceProxy.notifyTaskCompletion(taskMapper.toResource(task));
        }
       
        return task;
    }

    public Task stopTask(Long taskId, TaskResultResource taskResultResource) {
        Task task = readTask(taskId);
        return stopTask(task, taskResultResource);
    }

    public Task stopTask(Task task, TaskResultResource taskResultResource) {
		try {

            // Ask to the DevOps provider the real status of the Task
            TaskStatus taskRealStatus = null;
            odmExecutor = clients.getExecutorClient(task.getExecutorRef());
            if (odmExecutor != null && odmExecutor.getCheckAfterCallback()) {
                taskRealStatus = odmExecutor.readTaskStatus(task.getId());
            }

            if(!(taskResultResource == null)) {
                if(taskResultResource.getStatus() == null)
                    throw new UnprocessableEntityException(
                            DevOpsApiStandardErrors.SC422_03_TASK_RESULT_IS_INVALID,
                            "Task status cannot be null"
                    );
                if(taskResultResource.getStatus().equals(TaskResultStatus.PROCESSED)) {
                    if (taskRealStatus != null && taskRealStatus.equals(TaskStatus.FAILED)) {
                        task.setStatus(ActivityTaskStatus.FAILED);
                        task.setErrors("Check of the Task Status on the DevOps provider [" + task.getExecutorRef() +
                                "] return a FAILURE. Check the provider for any extra information.");
                    } else if (taskRealStatus != null && taskRealStatus.equals(TaskStatus.ABORTED)) {
                        task.setStatus(ActivityTaskStatus.ABORTED);
                        task.setErrors("Check of the Task Status on the DevOps provider [" + task.getExecutorRef() +
                                "] return an ABORTED. Check the provider for any extra information.");
                    } else {
                        task.setStatus(ActivityTaskStatus.PROCESSED);
                        task.setResults(ObjectMapperFactory.JSON_MAPPER.writeValueAsString(taskResultResource.getResults()));
                    }
                }
                else {
                    if (taskRealStatus != null && taskRealStatus.equals(TaskStatus.PROCESSED)) {
                        task.setStatus(ActivityTaskStatus.PROCESSED);
                    } else if (taskRealStatus != null && taskRealStatus.equals(TaskStatus.ABORTED)) {
                        task.setStatus(ActivityTaskStatus.ABORTED);
                        task.setErrors("Check of the Task Status on the DevOps provider [" + task.getExecutorRef() +
                                "] return an ABORTED. Check the provider for any extra information.");
                    } else {
                        task.setStatus(ActivityTaskStatus.FAILED);
                        task.setErrors(taskResultResource.getErrors());
                        // Save results even for FAILED status
                        if (!CollectionUtils.isEmpty(taskResultResource.getResults())) {
                            task.setResults(ObjectMapperFactory.JSON_MAPPER.writeValueAsString(taskResultResource.getResults()));
                        }
                    }
                }

                // Interactions with PolicyService
                if(!policyServiceProxy.isCallbackResultValid(taskMapper.toResource(task))){
                    throw new InternalServerException(
                            ODMApiCommonErrors.SC500_73_POLICY_SERVICE_EVALUATION_ERROR,
                            "Some blocking policy has not passed evaluation"
                    );
                }

            } else {
                if (taskRealStatus != null && taskRealStatus.equals(TaskStatus.FAILED)) {
                    task.setStatus(ActivityTaskStatus.FAILED);
                    task.setErrors("Check of the Task Status on the DevOps provider [" + task.getExecutorRef() +
                            "] return a FAILURE. Check the provider for any extra information.");
                } else if (taskRealStatus != null && taskRealStatus.equals(TaskStatus.ABORTED)) {
                    task.setStatus(ActivityTaskStatus.ABORTED);
                    task.setErrors("Check of the Task Status on the DevOps provider [" + task.getExecutorRef() +
                            "] return an ABORTED. Check the provider for any extra information.");
                } else {
                    task.setStatus(ActivityTaskStatus.PROCESSED);
                    taskResultResource = new TaskResultResource();
                    taskResultResource.setStatus(TaskResultStatus.PROCESSED);
                    Map<String, Object> results = new HashMap<>();
                    results.put("message", "OK");
                    taskResultResource.setResults(results);
                    task.setResults(taskResultResource.toJsonString());
                }
            }

            task.setFinishedAt(now());
            task = saveTask(task);

            devOpsNotificationServiceProxy.notifyTaskCompletion(taskMapper.toResource(task));

        } catch(Throwable t) {
             throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                "An error occurred in the backend database while saving task",
                t);
        }
        
        return task;
	}

    public Task abortTask(Long taskId) {
        Task task = readTask(taskId);
        if (!task.getStatus().equals(ActivityTaskStatus.PROCESSING) && 
            !task.getStatus().equals(ActivityTaskStatus.PLANNED)) return task;
        task.setStatus(ActivityTaskStatus.ABORTED);
        task.setFinishedAt(now());
        task = saveTask(task);
        devOpsNotificationServiceProxy.notifyTaskCompletion(taskMapper.toResource(task));
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
                    "An error occurred in the backend database while loading tasks",
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
                    "An error occurred in the backend database while loading task with id [" + taskId
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
                    "An error occurred in the backend database while searching tasks",
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
                activityId, executorRef, status), Sort.by(Sort.Order.asc("id")));
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
            if(StringUtils.hasText(template.getName())){
                task.setName(template.getName());
            }
            if(StringUtils.hasText(template.getDescription())){
                task.setDescription(template.getDescription());
            }
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

        String templateId = identifierStrategy.getId(
             template.getFullyQualifiedName());

        try {
            templateDefinition = clients.getRegistryClient().readTemplate(templateId);
            logger.debug("Template definition [" + templateId + "] successfully read from ODM Registry");
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "An error occurred in the backend service while loading template [" + templateId + "]",
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
                    "An error occurred in the backend service while parsing configurations [" + configurations + "]",
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
