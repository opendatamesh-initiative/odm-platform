package org.opendatamesh.platform.pp.devops.server.services;


import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.core.dpds.model.InfrastructuralComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.InternalComponentsDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleActivityInfoDPDS;
import org.opendatamesh.platform.core.dpds.model.LifecycleInfoDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.server.configurations.ProductPlaneProperties;
import org.opendatamesh.platform.pp.devops.server.database.entities.Activity;
import org.opendatamesh.platform.pp.registry.api.v1.clients.RegistryClient;
import org.opendatamesh.platform.up.executor.api.clients.ExecutorClient;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ActivityService {

    @Autowired
    ProductPlaneProperties utilityPlaneProperties;

    ExecutorClient executor;

    @Autowired
    RegistryClient registry;

    //private static final Logger logger = LoggerFactory.getLogger(ActivityService.class);

    public ActivityService() { }

    public ActivityResource createActivity(Activity activity,
            boolean startAfterCreation) {

        DataProductVersionDPDS dataProductVersion = registry.readOneDataProductVersion(activity.getDataProductId(), activity.getDataProductVersion());
        
        InternalComponentsDPDS internalComponents = dataProductVersion.getInternalComponents();
        LifecycleInfoDPDS lifecycleInfo = internalComponents.getLifecycleInfo();
        LifecycleActivityInfoDPDS activityInfo = lifecycleInfo.getActivityInfo(activity.getType());
       
        if(activityInfo == null) {
            // ERRORE
        }
        
        String serviceRef = activityInfo.getService().getHref();
        if(serviceRef.equalsIgnoreCase("azuredevops")) {
            executor = new ExecutorClient("http://localhost:8482");
            TaskResource task = new TaskResource();
            
            // TODO read template form registry
            activityInfo.getTemplate().getDefinition().getRef();
            task.setTemplate(null);

            ObjectMapper mapper = new ObjectMapper();
            try {
                String jsonString = mapper.writeValueAsString(activityInfo.getConfigurations());
                task.setConfigurations(jsonString);
            } catch (JsonProcessingException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            // create activity
            // submit task
            // save task
            // save activity-task relationship
            
        } else {
            throw new RuntimeException("Not supported");
        }
        return null;
    }
   
}
