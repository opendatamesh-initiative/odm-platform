package org.opendatamesh.platform.pp.devops.server.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.opendatamesh.platform.core.commons.servers.exceptions.*;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.LifecycleInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.LifecycleTaskInfoDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityTaskStatus;
import org.opendatamesh.platform.pp.devops.api.resources.DevOpsApiStandardErrors;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsClients;
import org.opendatamesh.platform.pp.devops.server.database.entities.Activity;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.opendatamesh.platform.pp.devops.server.database.repositories.ActivityRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ActivityService {

    @Autowired
    ActivityRepository activityRepository;

    @Autowired
    TaskService taskService;

    @Autowired
    DevOpsClients clients;

    @Autowired
    LifecycleService lifecycleService;

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
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Activity object cannot be null");
        }

        List<LifecycleTaskInfoDPDS> activitiesInfo = readTasksInfo(activity);
        if (activitiesInfo == null || activitiesInfo.isEmpty()) {
            throw new UnprocessableEntityException(
                    DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID,
                    "Liefecycle stage [" + activity.getStage() + "] not defined for version ["
                            + activity.getDataProductVersion() + "] of product [" + activity.getDataProductId() + "]");
        }

        activity.setStatus(ActivityStatus.PLANNED);
        List<Activity> activities = searchActivities(
                activity.getDataProductId(), activity.getDataProductVersion(), activity.getStage(),
                activity.getStatus());

        if (activities != null && activities.isEmpty() == false) {
            throw new UnprocessableEntityException(
                    DevOpsApiStandardErrors.SC422_02_ACTIVITY_ALREADY_EXISTS,
                    "Activity for stage [" + activity.getStage() + "] of version ["
                            + activity.getDataProductVersion() + "] of product [" + activity.getDataProductId()
                            + "] already exist");
        }

        try {
            activity = saveActivity(activity);
            logger.info("Activity [" + activity.getStage() + "] "
                    + "on version [" + activity.getDataProductVersion() + "] "
                    + "of product [" + activity.getDataProductId() + "] succesfully created");
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while saving activity [" + activity.getStage() + "] "
                            + "on version [" + activity.getDataProductVersion() + "] "
                            + "of product [" + activity.getDataProductId() + "]",
                    t);
        }

        // create tasks associated with the given activity
        List<Task> tasks = taskService.createTasks(activity.getId(), activitiesInfo);
    
        if (startAfterCreation) {
            activity = startActivity(activity, tasks);
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

        activity = readActivity(activityId);
        activity = startActivity(activity);

        return activity;
    }

    public Activity startActivity(Activity activity) {

        if(activity.getStatus().equals(ActivityStatus.PLANNED) == false) {
            if(activity.getStatus().equals(ActivityStatus.PROCESSING)) {
                throw new ConflictException(
                DevOpsApiStandardErrors.SC409_01_CONCURRENT_ACTIVITIES,
                "Activity with id [" + activity.getId() + "] is already started");
            } else {
                throw new UnprocessableEntityException(
                DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID,
                "Only activities in PLANNED state can be started. Activity with id [" + activity.getId() + "] is in state [" + activity.getStatus() + "]");
            }
            
        }

        List<Task> plannedTasks = taskService.searchTasks(activity.getId(), null, ActivityTaskStatus.PLANNED);
        return startActivity(activity, plannedTasks);
    }

    public Activity startActivity(Activity activity, List<Task> plannedTasks) {

        if(activity == null) {
           throw new InternalServerException(
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                "Activity object cannot be null");
        }

        // verify if there is an alredy running activity
        List<Activity> activities = searchActivities(
                activity.getDataProductId(),
                activity.getDataProductVersion(),
                null,
                ActivityStatus.PROCESSING);
        if (activities != null && !activities.isEmpty()) {
            throw new ConflictException(
                    DevOpsApiStandardErrors.SC409_01_CONCURRENT_ACTIVITIES,
                    "There is already a running activity on version [" + activity.getDataProductId()
                            + "] of data product [" + activity.getDataProductVersion() + "]");
        }

        // TODO validate stage transition with policy engine (FROM stage TO stage)

        // update activity's status
        try {
            activity.setStartedAt(now());
            activity.setStatus(ActivityStatus.PROCESSING);
            activity = saveActivity(activity);
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                   "An error occured in the backend database while updating activity [" + activity.getId() + "]",
                 t);
        }       
        
        // start next planned task if any
        startNextPlannedTaskAndUpdateParentActivity(activity.getId());
        
        return activity;
    }

    private Activity stopActivity(Long activityId, boolean success) {
        Activity activity = readActivity(activityId);
        return stopActivity(activity, success);
    }

    // TODO set results or errors of activity while stopping it
    private Activity stopActivity(Activity activity, boolean success) {
        
        LocalDateTime finishedAt = now();
        activity.setFinishedAt(finishedAt);

        List<Task> tasks = taskService.searchTasks(activity.getId(), null, null);
        for(Task task: tasks) {
            if(task.getStatus().equals(ActivityTaskStatus.PLANNED) 
            || task.getStatus().equals(ActivityTaskStatus.PROCESSING)) {
                task.setStatus(ActivityTaskStatus.ABORTED);
                task.setFinishedAt(finishedAt);
                taskService.saveTask(task);
            }
        }

        ObjectNode activityOutputNode = ObjectMapperFactory.JSON_MAPPER.createObjectNode();
        if (success) {
            for(Task task: tasks) {
                if(task.getStatus().equals(ActivityTaskStatus.PROCESSED)) {
                    activityOutputNode.put(task.getId().toString(), task.getResults());
                }
            }
            try {
				String output = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(activityOutputNode);
                activity.setResults(output);
            } catch (JsonProcessingException e) {
				logger.warn("Impossible to serialize results aggregate", e);
			}

            activity.setStatus(ActivityStatus.PROCESSED);
            lifecycleService.createLifecycle(activity);

        } else {
            for(Task task: tasks) {
                if(task.getStatus().equals(ActivityTaskStatus.FAILED)) {
                    activityOutputNode.put(task.getId().toString(), task.getErrors());
                }
            }
            try {
				String output = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(activityOutputNode);
                activity.setErrors(output);
            } catch (JsonProcessingException e) {
				logger.warn("Impossible to serialize errors aggregate", e);
			}

            activity.setStatus(ActivityStatus.FAILED);
        }
        activity = saveActivity(activity);

        return activity;
    }

    public Task startNextPlannedTaskAndUpdateParentActivity(Long activityId) {
        Task startedTask = null;

        List<Task> plannedTasks = taskService.searchTasks(activityId, null, ActivityTaskStatus.PLANNED);
        if (plannedTasks != null && !plannedTasks.isEmpty()) {
            startedTask = taskService.startTask(plannedTasks.get(0)); 
            if(startedTask.getStatus().equals(ActivityTaskStatus.FAILED)) {
                stopActivity(activityId, false);
            } else if(startedTask.getStatus().equals(ActivityTaskStatus.PROCESSED)) {
                stopActivity(activityId, true);
            }
        } else { // nothing more to do...
            stopActivity(activityId, true);
        }

        return startedTask;
    }

    public Task stopTaskAndUpdateParentActivity(Long taskId) {
        Task task = taskService.stopTask(taskId);
        startNextPlannedTaskAndUpdateParentActivity(task.getActivityId());
        return task;
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
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
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
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Activity object cannot be null");
        }
        return readActivity(activity.getId());
    }

    public Activity readActivity(Long activityId) {

        Activity activity = null;

        if (activityId == null) {
            throw new BadRequestException(
                    DevOpsApiStandardErrors.SC400_50_ACTIVITY_ID_IS_EMPTY,
                    "Activity id is empty");
        }

        try {
            activity = loadActivity(activityId);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while loading activity with id [" + activityId
                            + "]",
                    t);
        }

        if (activity == null) {
            throw new NotFoundException(
                    DevOpsApiStandardErrors.SC404_01_ACTIVITY_NOT_FOUND,
                    "Activity with id equals to [" + activityId + "] does not exist");
        }

        return activity;
    }

    private Activity loadActivity(Long activitytId) {
        Activity activity = null;

        Optional<Activity> activityLookUpResults = activityRepository.findById(activitytId);

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
                ODMApiCommonErrors.SC500_00_SERVICE_ERROR,
                    "Activity object cannot be null");
        }

        return activityRepository.existsById(activityId);
    }

    // -------------------------
    // search methods
    // -------------------------
    public List<Activity> searchActivities(
            String dataProductId,
            String dataProductVersion,
            String stage,
            ActivityStatus status) {
        List<Activity> activitySearchResults = null;
        try {
            activitySearchResults = findActivities(dataProductId, dataProductVersion, stage, status);
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching activities",
                    t);
        }
        return activitySearchResults;
    }

    private List<Activity> findActivities(
            String dataProductId,
            String dataProductVersion,
            String stage,
            ActivityStatus status) {

        return activityRepository
                .findAll(ActivityRepository.Specs.hasMatch(
                        dataProductId, dataProductVersion, stage, status));
    }

    // -------------------------
    // other methods
    // -------------------------

    private List<LifecycleTaskInfoDPDS> readTasksInfo(Activity activity) {

        List<LifecycleTaskInfoDPDS> tasksInfoRes = new ArrayList<LifecycleTaskInfoDPDS>();

        if (!StringUtils.hasText(activity.getStage())) {
            throw new UnprocessableEntityException(
                    DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID,
                    "Activity type property cannot be empty");
        }

        DataProductVersionDPDS dataProductVersion = readDataProductVersion(activity);
        if (dataProductVersion == null) {
            throw new UnprocessableEntityException(
                    DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID,
                    "The version [" + activity.getDataProductVersion() + "] of data product ["
                            + activity.getDataProductId() + "] pointed by activity does not exist");
        }

        if (dataProductVersion.hasLifecycleInfo() == false) {
            throw new UnprocessableEntityException(
                    DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID,
                    "Liefecycle info not defined for version [" + activity.getDataProductVersion() + "] of product ["
                            + activity.getDataProductId() + "]");
        }

        LifecycleInfoDPDS lifecycleInfo = dataProductVersion.getInternalComponents().getLifecycleInfo();
        List<LifecycleTaskInfoDPDS> stageTasksInfoRes = lifecycleInfo.getTasksInfo(activity.getStage());
        if(stageTasksInfoRes != null) {
            tasksInfoRes.addAll(stageTasksInfoRes);
        }
        if(tasksInfoRes.size() > 1) { // for the moment ...
            throw new UnprocessableEntityException(
                    DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID,
                    "Devops module is unable to prcess activities with more than one task associated");
        }
        

        return tasksInfoRes;
    }

    private DataProductVersionDPDS readDataProductVersion(Activity activity) {

        DataProductVersionDPDS dataProductVersion = null;

        if (!StringUtils.hasText(activity.getDataProductId())) {
            throw new UnprocessableEntityException(
                    DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID,
                    "Activity productId property cannot be empty");
        }

        if (!StringUtils.hasText(activity.getDataProductVersion())) {
            throw new UnprocessableEntityException(
                    DevOpsApiStandardErrors.SC422_01_ACTIVITY_IS_INVALID,
                    "Activity dataProductVersion property cannot be empty");
        }

        try {
            dataProductVersion = clients.getRegistryClient().readOneDataProductVersion(
                    activity.getDataProductId(),
                    activity.getDataProductVersion());
        } catch (Throwable t) {
            throw new InternalServerException(
                ODMApiCommonErrors.SC500_50_REGISTRY_SERVICE_ERROR,
                    "An errror occured while reading data product version from ODM Registry", t);
        }

        return dataProductVersion;
    }

    private LocalDateTime now() {
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 
        now.getHour(), now.getMinute(), now.getSecond(), 0);
        return now;
    }
}
