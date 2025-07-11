package org.opendatamesh.platform.pp.devops.server.controllers;

import org.opendatamesh.platform.pp.devops.api.controllers.AbstractActivityController;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatus;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityStatusResource;
import org.opendatamesh.platform.pp.devops.server.database.entities.Activity;
import org.opendatamesh.platform.pp.devops.server.database.mappers.ActivityMapper;
import org.opendatamesh.platform.pp.devops.server.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActivitiesController extends AbstractActivityController {

    @Autowired
    ActivityService activityService;

    @Autowired
    ActivityMapper activityMapper;

    @Override
    public ActivityResource createActivity(
        ActivityResource activityRes,
        boolean startAfterCreation)
    {
        Activity activity = activityService.createActivity(activityMapper.toEntity(activityRes), startAfterCreation);
        return activityMapper.toResource(activity);
    }

    @Override
    public ActivityStatusResource startActivity(Long id) {
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
}
