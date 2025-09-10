package org.opendatamesh.platform.pp.devops.server.controllers;

import org.opendatamesh.platform.pp.devops.api.controllers.AbstractActivityController;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatusResource;
import org.opendatamesh.platform.pp.devops.server.database.entities.Activity;
import org.opendatamesh.platform.pp.devops.server.database.mappers.ActivityMapper;
import org.opendatamesh.platform.pp.devops.server.services.ActivityService;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
public class ActivitiesController extends AbstractActivityController {

    private static final Logger logger = LoggerFactory.getLogger(ActivitiesController.class);

    @Autowired
    ActivityService activityService;

    @Autowired
    ActivityMapper activityMapper;

    @Override
    public ActivityResource createActivity(
        ActivityResource activityRes,
        boolean startAfterCreation,
        Map<String, String> headers)
    {
        // Create the activity first
        Activity activity = activityService.createActivity(activityMapper.toEntity(activityRes), startAfterCreation);
        
        // Process executor secrets and store them in cache
        processExecutorSecrets(headers, activity.getId());
        
        // Start the activity if requested (after secrets are processed)
        if (startAfterCreation) {
            activity = activityService.startActivity(activity);
        }
        
        return activityMapper.toResource(activity);
    }

    @Override
    public ActivityStatusResource startActivity(Long id, Map<String, String> headers) {
        // Process executor secrets and store them in cache
        processExecutorSecrets(headers, id);
        
        // Start the activity
        Activity activity = activityService.startActivity(id);
        ActivityStatusResource statusRes = new ActivityStatusResource();
        statusRes.setStatus(activity.getStatus());
        return statusRes;
    }

    @Override
    public ActivityStatusResource abortActivity(Long activityId) {
        Activity activity = activityService.abortActivity(activityId);
        ActivityStatusResource statusResource = new ActivityStatusResource();
        statusResource.setStatus(activity.getStatus());
        return statusResource;
    }

    @Override
    public ActivityStatusResource readActivityStatus(Long id) {
       Activity activity = activityService.readActivity(id);
       activity.getStatus().toString();
       ActivityStatusResource statusRes = new ActivityStatusResource();
       statusRes.setStatus(activity.getStatus());
       return statusRes;
    }

    @Override
    public List<ActivityResource> readActivities(
        String dataProductId,
        String dataProductVersion,
        String stage,
        ActivityStatus status)
    {
        List<Activity> activities = null;

        if(dataProductId != null || dataProductVersion != null || stage != null || status != null) {
            activities = activityService.searchActivities(dataProductId, dataProductVersion, stage, status);
        } else {
            activities = activityService.readAllActivities();
        }

        return activityMapper.toResources(activities);
    }

    @Override
    public ActivityResource readActivitiy(Long id) {
        Activity activity = activityService.readActivity(id);
        return activityMapper.toResource(activity);
    }

    @Override
    public ActivityResource deleteActivity(Long id) {
        return activityService.deleteActivity(id);
    }

    @Override
    public ActivityResource updateActivity(Long id, ActivityResource activityRes) {
        Activity activity = activityService.updateActivity(id, activityMapper.toEntity(activityRes));
        return activityMapper.toResource(activity);
    }

    // ======================================================================================
    // EXECUTOR SECRETS PROCESSING
    // ======================================================================================

    /**
     * Processes executor secrets from HTTP headers and stores them in the cache.
     * This method extracts headers matching the pattern "x-odm-<executorName>-executor-secret-<secretType>"
     * and transforms them to "x-odm-<secretType>" for storage in the cache.
     * 
     * @param headers The HTTP headers containing executor secrets
     * @param activityId The activity ID to associate the secrets with
     */
    private void processExecutorSecrets(Map<String, String> headers, Long activityId) {
        if (headers == null || activityId == null) {
            return;
        }

        // Group secrets by executor name
        Map<String, Map<String, String>> executorSecrets = new HashMap<>();

        for (Map.Entry<String, String> header : headers.entrySet()) {
            String headerName = header.getKey();
            String headerValue = header.getValue();

            // Filter headers that match the pattern: x-odm-<executorName>-executor-secret-<secretType>
            if (headerName.startsWith("x-odm-") && headerName.contains("-executor-secret-")) {
                // Extract executor name: between "x-odm-" and "-executor-secret-"
                String executorSecretMarker = "-executor-secret-";
                int executorSecretIndex = headerName.indexOf(executorSecretMarker);
                
                if (executorSecretIndex > 6) { // 6 is length of "x-odm-"
                    String executorName = headerName.substring(6, executorSecretIndex); // 6 is length of "x-odm-"
                    
                    // Extract secret type: everything after "-executor-secret-"
                    String secretType = headerName.substring(executorSecretIndex + executorSecretMarker.length());
                    
                    // Create transformed header name: x-odm-<secretType>
                    String transformedHeaderName = "x-odm-" + secretType;

                    // Store in executor secrets map
                    executorSecrets.computeIfAbsent(executorName, k -> new HashMap<>())
                            .put(transformedHeaderName, headerValue);
                }
            }
        }

        // Store secrets in cache for each executor
        for (Map.Entry<String, Map<String, String>> executorEntry : executorSecrets.entrySet()) {
            String executorName = executorEntry.getKey();
            Map<String, String> secrets = executorEntry.getValue();
            
            DevOpsClients.storeSecrets(executorName, activityId, secrets);
            logger.debug("Stored {} secrets for executor '{}' and activity {}", 
                        secrets.size(), executorName, activityId);
        }
    }
}
