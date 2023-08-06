package org.opendatamesh.platform.pp.devops.server.controllers;

import org.opendatamesh.platform.pp.devops.api.controllers.AbstractDevOpsController;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.server.database.entities.Activity;
import org.opendatamesh.platform.pp.devops.server.database.mappers.ActivityMapper;
import org.opendatamesh.platform.pp.devops.server.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ActivitiesController extends AbstractDevOpsController {

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
    public ActivityResource startActivity(Long id) {
        Activity activity = activityService.startActivity(id);
        return activityMapper.toResource(activity);
    }

    @Override
    public String readActivityStatus(Long id) {
       Activity activity = activityService.readActivity(id);
       return activity.getStatus().toString();
    }

    @Override
    public List<ActivityResource> readActivities() {
        List<Activity> activities = activityService.readAllActivities();
        return activityMapper.toResources(activities);
    }

    @Override
    public ActivityResource readActivitiy(Long id) {
        Activity activity = activityService.readActivity(id);
        return activityMapper.toResource(activity);
    }
}
