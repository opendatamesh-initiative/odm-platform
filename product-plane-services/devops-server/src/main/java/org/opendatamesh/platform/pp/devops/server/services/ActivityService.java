package org.opendatamesh.platform.pp.devops.server.services;



import org.opendatamesh.platform.core.dpds.model.DataProductVersionDPDS;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.pp.devops.server.database.entities.Activity;
import org.opendatamesh.platform.pp.registry.api.v1.clients.RegistryClient;
import org.opendatamesh.platform.up.executor.api.clients.ExecutorClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ActivityService {

    @Value("${odm.utilityPlane.executorServices.azureDevOps.address}")
    private String registryAddress;

    ExecutorClient executor;
    RegistryClient registry;

    public ActivityService() {
        registry = new RegistryClient(registryAddress);
    }

    public ActivityResource createActivity(Activity activity,
            boolean startAfterCreation) {

        DataProductVersionDPDS dataProductVersion = registry.readDataProductVersion(Long.parseLong(activity.getDataProductId()), activity.getDataProductVersion());
        
        dataProductVersion.getInternalComponents().getApplicationComponents();
        executor = new ExecutorClient(null);
        return null;
    }
   
}
