package org.opendatamesh.platform.pp.devops.server.controllers;

import java.util.List;

import org.opendatamesh.platform.pp.devops.api.controllers.AbstractDevOpsController;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.server.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ActivitiesController extends AbstractDevOpsController {

    @Autowired
    ActivityService activityService;

    @Override
    public ActivityResource createActivity(ActivityResource activity,
            boolean startAfterCreation) {
        activityService.createActivity(null, startAfterCreation);
        throw new UnsupportedOperationException("Unimplemented method 'createActivity'");
    }

    @Override
    public ActivityResource startActivity(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'startActivity'");
    }

    @Override
    public ActivityResource stopActivity(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'stopActivity'");
    }

    @Override
    public ActivityResource readActivityStatus(Long id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readActivityStatus'");
    }

    @Override
    public List<ActivityResource> readActivities() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'readActivities'");
    }

    @Override
    public List<ActivityResource> readActivitiy(Long id) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'readActivitiy'");
        activityService.createActivity(null, true);
        return null;
    }
    
}
