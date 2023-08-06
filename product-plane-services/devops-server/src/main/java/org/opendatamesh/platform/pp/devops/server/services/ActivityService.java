package org.opendatamesh.platform.pp.devops.server.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.StandardDefinitionDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ODMDevOpsAPIStandardError;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsConfigurations;
import org.opendatamesh.platform.pp.devops.server.database.entities.Activity;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.opendatamesh.platform.pp.devops.server.database.mappers.ActivityMapper;
import org.opendatamesh.platform.pp.devops.server.database.repositories.ActivityRepository;
import org.opendatamesh.platform.pp.devops.server.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.devops.server.exceptions.ConflictException;
import org.opendatamesh.platform.pp.devops.server.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.devops.server.exceptions.NotFoundException;
import org.opendatamesh.platform.pp.devops.server.exceptions.UnprocessableEntityException;
import org.opendatamesh.platform.pp.registry.api.v1.resources.DefinitionResource;
import org.opendatamesh.platform.up.executor.api.clients.ExecutorClient;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class ActivityService {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    ActivityMapper activityMapper;

    @Autowired
    DevOpsConfigurations configurations;

    private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    public ActivityService() {

    }

    // ======================================================================================
    // CREATE
    // ======================================================================================

    public Activity createActivity(
            Activity activity,
            boolean startAfterCreation) {

        if (activity == null) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Activity object cannot be null");
        }


        List<LifecycleActivityInfoDPDS> activitiesInfo = readActivitiesInfo(activity);
        if (activitiesInfo == null || activitiesInfo.isEmpty())  {
            throw new UnprocessableEntityException(
                    ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID,
                    "Liefecycle stage [" + activity.getType() + "] not defined for version ["
                            + activity.getDataProductVersion() + "] of product [" + activity.getDataProductId() + "]");
        }

        activity.setStatus(ActivityStatus.PLANNED);
        List<Activity> activities = searchActivities(
                activity.getDataProductId(), activity.getDataProductVersion(), activity.getType(),
                activity.getStatus());

        if (activities != null && activities.isEmpty() == false) {
            throw new UnprocessableEntityException(
                    ODMDevOpsAPIStandardError.SC422_02_ACTIVITY_ALREADY_EXISTS,
                    "Activity for stage [" + activity.getType() + "] of version ["
                            + activity.getDataProductVersion() + "] of product [" + activity.getDataProductId()
                            + "] already exist");
        }

        try {
            activity = saveActivity(activity);
            logger.info("Activity [" + activity.getType() + "] "
                    + "on version [" + activity.getDataProductVersion() + "] "
                    + "of product [" + activity.getDataProductId() + "] succesfully created");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving activity [" + activity.getType() + "] "
                            + "on version [" + activity.getDataProductVersion() + "] "
                            + "of product [" + activity.getDataProductId() + "]",
                    t);
        }

        if (startAfterCreation) {
            startActivity(activity, activitiesInfo);
        }

        return activity;
    }

    private Activity saveActivity(Activity activity) {
        return activityRepository.saveAndFlush(activity);
    }

    // ======================================================================================
    // START/STOP
    // ======================================================================================
    public Activity startActivity(Long activityId) {
        Activity activity = null;

        if (activityId == null) {
            throw new BadRequestException(
                    ODMDevOpsAPIStandardError.SC400_01_ACTIVITY_ID_IS_EMPTY,
                    "Activity id is empty");
        }

        activity = readActivity(activityId);
        if (activity == null) {
            throw new NotFoundException(
                    ODMDevOpsAPIStandardError.SC404_01_ACTIVITY_NOT_FOUND,
                    "Activity with id [" + activity.getId() + "] does not existe");
        }

        activity = startActivity(activity);
        return activity;
    }

    public Activity startActivity(Activity activity) {
        List<LifecycleActivityInfoDPDS> activitiesInfo = readActivitiesInfo(activity);
        return startActivity(activity, activitiesInfo);
    }

    public Activity startActivity(Activity activity, List<LifecycleActivityInfoDPDS> activitiesInfo) {

        // verify if there is an alredy running activity
        List<Activity> activities = searchActivities(
                activity.getDataProductId(),
                activity.getDataProductVersion(),
                null,
                ActivityStatus.PROCESSING);
        if (activities != null && !activities.isEmpty()) {
            throw new ConflictException(
                    ODMDevOpsAPIStandardError.SC409_01_CONCURRENT_ACTIVITIES,
                    "There is already a running activity on version [" + activity.getDataProductId()
                            + "] of data product [" + activity.getDataProductVersion() + "]");
        }

        // TODO validate stage transition with policy engine (FROM stage TO stage)

        activity = createTasks(activity, activitiesInfo);
        Task task = activity.getNextPlannedTask();
        if (task != null) {
            startTask(activity, task);
        } else {
            stopActivity(activity, true);
        }

        return activity;
    }

    private Task startTask(Activity activity, Task taskToStart) {
        
        try {
            taskToStart = startTask(taskToStart);
            taskToStart.setStartedAt(new Date());  
            saveActivity(activity);
        } catch (Throwable t2) { 
            taskToStart.setStatus(TaskStatus.FAILED);
            taskToStart.setErrors(t2.getMessage());
            taskToStart.setStartedAt(new Date());
            taskToStart.setFinishedAt(taskToStart.getStartedAt()); 
            stopActivity(activity, false);
        }

        if (taskToStart.getStatus().equals(TaskStatus.FAILED)) {
            taskToStart = activity.getTask(taskToStart.getId());
            taskToStart.setStatus(TaskStatus.FAILED);
            taskToStart.setStartedAt(new Date());
            taskToStart.setFinishedAt(taskToStart.getStartedAt()); 
            stopActivity(activity, false);
        }

        return taskToStart;
    }

    // Create tasks and start activity
    private Activity createTasks(Activity activity, List<LifecycleActivityInfoDPDS> activitiesInfo) {

        for (LifecycleActivityInfoDPDS activityInfo : activitiesInfo) {
            Task task = createTask(activityInfo);
            task.setStatus(TaskStatus.PLANNED);
            activity.getTasks().add(task);
        }

        try {
            activity.setStartedAt(new Date());
            activity.setStatus(ActivityStatus.PROCESSING);
            activity = saveActivity(activity);
            logger.info("Activity [" + activity.getType() + "] "
                    + "on version [" + activity.getDataProductVersion() + "] "
                    + "of product [" + activity.getDataProductId() + "] succesfully updated");

        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving activity [" + activity.getType() + "] "
                            + "on version [" + activity.getDataProductVersion() + "] "
                            + "of product [" + activity.getDataProductId() + "]",
                    t);
        }

        return activity;
    }

    private Activity stopActivity(Activity activity, boolean success) {
        activity.setFinishedAt(new Date());
        if (success) {
            activity.setStatus(ActivityStatus.PROCESSED);
        } else {
            activity.setStatus(ActivityStatus.FAILED);
        }
        activity = saveActivity(activity);

        return activity;
    }

    // ======================================================================================
    // READ
    // ======================================================================================

    public List<Activity> readAllActivities() {
        List<Activity> activities = null;
        try {
            activities = loadAllActivities();
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading activity",
                    t);
        }
        return activities;
    }

    private List<Activity> loadAllActivities() {
        return activityRepository.findAll();
    }

    public Activity readActivity(Activity activity) {
        if (activity == null) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Activity object cannot be null");
        }
        return readActivity(activity.getId());
    }

    public Activity readActivity(Long activityId) {

        Activity activity = null;

        if (activityId == null) {
            throw new BadRequestException(
                    ODMDevOpsAPIStandardError.SC400_01_ACTIVITY_ID_IS_EMPTY,
                    "Activity id is empty");
        }

        try {
            activity = loadActivity(activityId);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading data product with id [" + activityId
                            + "]",
                    t);
        }

        if (activity == null) {
            throw new NotFoundException(
                    ODMDevOpsAPIStandardError.SC404_01_ACTIVITY_NOT_FOUND,
                    "Data Product with [" + activityId + "] does not exist");
        }

        return activity;
    }

    private Activity loadActivity(Long activitytId) {
        Activity activity = null;

        Optional<Activity> activityLookUpResults = activityRepository.findById(activitytId.toString());

        if (activityLookUpResults.isPresent()) {
            activity = activityLookUpResults.get();
        }

        return activity;
    }

    // -------------------------
    // exists methods
    // -------------------------

    private boolean activityExists(Long activityId) {
        if (activityId == null) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_00_SERVICE_ERROR,
                    "Activity object cannot be null");
        }

        return activityRepository.existsById(activityId.toString());
    }

    public boolean activityExists(String activityId) {
        return activityRepository.existsById(activityId);
    }

    // -------------------------
    // search methods
    // -------------------------
    public List<Activity> searchActivities(
            String dataProductId,
            String dataProductVersion,
            String type,
            ActivityStatus status) {
        List<Activity> activitySearchResults = null;
        try {
            activitySearchResults = findActivities(dataProductId, dataProductVersion, type, status);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching activities",
                    t);
        }
        return activitySearchResults;
    }

    private List<Activity> findActivities(
            String dataProductId,
            String dataProductVersion,
            String type,
            ActivityStatus status) {

        return activityRepository
                .findAll(ActivityRepository.Specs.hasMatch(
                        dataProductId, dataProductVersion, type, status));
    }

    // -------------------------
    // other methods
    // -------------------------

    private List<LifecycleActivityInfoDPDS> readActivitiesInfo(Activity activity) {

        List<LifecycleActivityInfoDPDS> activitiesInfo = new ArrayList<LifecycleActivityInfoDPDS>();

        if (!StringUtils.hasText(activity.getType())) {
            throw new UnprocessableEntityException(
                    ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID,
                    "Activity type property cannot be empty");
        }

        DataProductVersionDPDS dataProductVersion = readDataProductVersion(activity);
        if (dataProductVersion == null) {
            throw new UnprocessableEntityException(
                    ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID,
                    "The version [" + activity.getDataProductVersion() + "] of data product ["
                            + activity.getDataProductId() + "] pointed by activity does not exist");
        }

        if (dataProductVersion.hasLifecycleInfo() == false) {
            throw new UnprocessableEntityException(
                    ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID,
                    "Liefecycle info not defined for version [" + activity.getDataProductVersion() + "] of product ["
                            + activity.getDataProductId() + "]");
        }

        LifecycleInfoDPDS lifecycleInfo = dataProductVersion.getInternalComponents().getLifecycleInfo();
        activitiesInfo.add( lifecycleInfo.getActivityInfo(activity.getType()) );

        return activitiesInfo;
    }

    private DataProductVersionDPDS readDataProductVersion(Activity activity) {

        DataProductVersionDPDS dataProductVersion = null;

        if (!StringUtils.hasText(activity.getDataProductId())) {
            throw new UnprocessableEntityException(
                    ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID,
                    "Activity productId property cannot be empty");
        }

        if (!StringUtils.hasText(activity.getDataProductVersion())) {
            throw new UnprocessableEntityException(
                    ODMDevOpsAPIStandardError.SC422_01_ACTIVITY_IS_INVALID,
                    "Activity dataProductVersion property cannot be empty");
        }

        try {
            dataProductVersion = configurations.getRegistryClient().readOneDataProductVersion(
                    activity.getDataProductId(),
                    activity.getDataProductVersion());
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_00_SERVICE_ERROR,
                    "An errror occured while reading data product version from ODM Registry", t);
        }

        return dataProductVersion;
    }

    private Task createTask(LifecycleActivityInfoDPDS activityInfo) {
        Task task = null;

        task = new Task();
        String executorServiceRef = activityInfo.getService().getHref();
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

    private Task startTask(Task task) {
        TaskResource taskRes = null;

        taskRes = activityMapper.toTaskResource(task);

        ExecutorClient odmExecutor = configurations.getExecutorClient(task.getExecutorRef());
        if (odmExecutor != null) {
            taskRes.setStartedAt(new Date());
            taskRes = odmExecutor.createTask(taskRes);
        } else {
            taskRes.setErrors("Executor [" + task.getExecutorRef() + "] supported");
        }

        return activityMapper.toTaskEntity(taskRes);
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
            templateDefinition = configurations.getRegistryClient().readOneTemplateDefinition(templateId);
            logger.debug("Template definition [" + templateId + "] succesfully read from ODM Registry");
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMDevOpsAPIStandardError.SC500_00_SERVICE_ERROR,
                    "An error occured in the backend service while loading template [" + templateId + "]",
                    t);
        }
        if (templateDefinition == null) {
            throw new NotFoundException(
                    ODMDevOpsAPIStandardError.SC404_01_ACTIVITY_NOT_FOUND,
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
