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
import org.opendatamesh.platform.pp.devops.api.resources.TaskResultResource;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsClients;
import org.opendatamesh.platform.pp.devops.server.database.entities.Activity;
import org.opendatamesh.platform.pp.devops.server.database.entities.Task;
import org.opendatamesh.platform.pp.devops.server.database.repositories.ActivityRepository;
import org.opendatamesh.platform.pp.devops.server.resources.context.ActivityContext;
import org.opendatamesh.platform.pp.devops.server.resources.context.ActivityResultStatus;
import org.opendatamesh.platform.pp.devops.server.resources.context.Context;
import org.opendatamesh.platform.pp.devops.server.utils.ObjectNodeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
            // Create context object for each Task
            Context taskContext = createContext(activityId);
            Task actualTask = plannedTasks.get(0);
            try {
                ObjectNode taskConfigs;
                if(actualTask.getConfigurations() != null) {
                    taskConfigs = ObjectMapperFactory.JSON_MAPPER.readValue(
                            actualTask.getConfigurations(),
                            ObjectNode.class
                    );
                } else {
                    taskConfigs = ObjectMapperFactory.JSON_MAPPER.createObjectNode();
                }
                taskConfigs.put("context", ObjectNodeUtils.toObjectNode(taskContext.getContext()));
                actualTask.setConfigurations(taskConfigs.toString());
            } catch (JsonProcessingException e) {
                logger.warn("Impossible to deserialize config attribute of task to append context", e);
            }
            startedTask = taskService.startTask(actualTask);
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

    public Task stopTaskAndUpdateParentActivity(Long taskId, TaskResultResource taskResultResource) {
        Task task = taskService.stopTask(taskId, taskResultResource);
        updateActivityPartialResults(task);
        startNextPlannedTaskAndUpdateParentActivity(task.getActivityId());
        return task;
    }

    public void updateActivityPartialResults(Task task) {
        if(
                task.getStatus().equals(ActivityTaskStatus.PROCESSED)
                        && task.getResults() != null
        ) {
            Activity parentActivity = readActivity(task.getActivityId());
            String partialActivityResults = parentActivity.getResults();
            ObjectNode activityOutputNode;
            String result = null;
            if(partialActivityResults == null) {
                activityOutputNode = ObjectMapperFactory.JSON_MAPPER.createObjectNode();
                try {
                    activityOutputNode.put("task1", ObjectNodeUtils.toObjectNode(task.getResults()));
                    result = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(activityOutputNode);
                } catch (JsonProcessingException e) {
                    logger.warn("Impossible to serialize results aggregate", e);
                }
            } else {
                try {
                    activityOutputNode = ObjectMapperFactory.JSON_MAPPER.readValue(partialActivityResults, ObjectNode.class);
                    int progressiveTaskNumber = findMaxTaskNumber(activityOutputNode);
                    activityOutputNode.put("task" + progressiveTaskNumber, ObjectNodeUtils.toObjectNode(task.getResults()));
                    result = ObjectMapperFactory.JSON_MAPPER.writeValueAsString(activityOutputNode);
                } catch (JsonProcessingException e) {
                    logger.warn("Impossible to deserialize previous results aggregate and/or aggregate new results to it", e);
                }
            }
            parentActivity.setResults(result);
            saveActivity(parentActivity);
        }
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
    // CONTEXT methods
    // -------------------------

    private Context createContext(Long activityId) {
        Activity currentActivity = readActivity(activityId);
        List<Activity> previousAndCurrentActivities = searchOrderedActivities(
                currentActivity.getDataProductId(),
                currentActivity.getDataProductVersion()
        );
        Context context = new Context();
        ActivityContext activityContext;
        for (Activity activity : previousAndCurrentActivities) {
            activityContext = new ActivityContext();
            ActivityResultStatus activityResultStatus = activity.getStatus().equals(ActivityStatus.PROCESSED) ?
                    ActivityResultStatus.PROCESSED :
                    ActivityResultStatus.PROCESSING;
            activityContext.setStatus(activityResultStatus);
            Date activityFinishedAt = activity.getFinishedAt() != null ?
                    Date.from(activity.getFinishedAt().atZone(ZoneId.systemDefault()).toInstant()) :
                    null;
            activityContext.setFinishedAt(activityFinishedAt);
            Map<String, Object> contextualizedActivityResults = null;
            if(activity.getResults() != null) {
                try {
                    contextualizedActivityResults = ObjectMapperFactory.JSON_MAPPER.readValue(
                            activity.getResults(),
                            Map.class
                    );
                } catch (JsonProcessingException e) {
                    logger.warn("Impossible to deserialize previous activity results to create context object", e);
                }
            }
            activityContext.setResults(contextualizedActivityResults);
            context.concatenateActivitiesContext(
                    activity.getStage(),
                    activityContext
            );
        }

        return context;
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

    public List<Activity> searchOrderedActivities(
            String dataProductId,
            String dataProductVersion
    ) {
        List<Activity> activitySearchResults = null;
        try {
            activitySearchResults = findActivities(dataProductId, dataProductVersion, null, null);
        } catch (Throwable t) {
            throw new InternalServerException(
                    ODMApiCommonErrors.SC500_01_DATABASE_ERROR,
                    "An error occured in the backend database while searching activities",
                    t);
        }
        if(activitySearchResults != null) {
            activitySearchResults = activitySearchResults.stream()
                    .filter(
                            activity ->
                                    activity.getStatus().equals(ActivityStatus.PROCESSED) ||
                                    activity.getStatus().equals(ActivityStatus.PROCESSING)
                    )
                    .collect(Collectors.toList());
            activitySearchResults.sort(Comparator.comparing(Activity::getId));
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

    public static int findMaxTaskNumber(ObjectNode jsonContent) {

        Map<String, Object> map = null;
        try {
            map = ObjectMapperFactory.JSON_MAPPER.readValue(jsonContent.toString(), Map.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        int maxTaskNumber = 0;
        Pattern pattern = Pattern.compile("task(\\d+)");

        for (String key : map.keySet()) {
            Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                int currentTaskNumber = Integer.parseInt(matcher.group(1));
                maxTaskNumber = Math.max(maxTaskNumber, currentTaskNumber);
            }
        }

        return maxTaskNumber;

    }

    private LocalDateTime now() {
        LocalDateTime now = LocalDateTime.now();
        now = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 
        now.getHour(), now.getMinute(), now.getSecond(), 0);
        return now;
    }
}