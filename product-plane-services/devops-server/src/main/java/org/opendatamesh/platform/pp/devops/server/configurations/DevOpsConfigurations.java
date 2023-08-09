package org.opendatamesh.platform.pp.devops.server.configurations;

import java.util.Map;

import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsConfigurations.ServiceConfigs;
import org.opendatamesh.platform.pp.registry.api.v1.clients.RegistryClient;
import org.opendatamesh.platform.up.executor.api.clients.ExecutorClient;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@ConfigurationProperties(prefix = "odm")
@Data
public class DevOpsConfigurations {
    
    ProductPlaneConfigs productPlane;
    UtiltyPlaneConfigs utilityPlane;

    public RegistryClient getRegistryClient() {
        RegistryClient client = null;

        client = new RegistryClient(productPlane.getRegistryService().getAddress());
        
        return client;
    }
    

    public ExecutorClient getExecutorClient(String adapterName) {
        ExecutorClient executorClient = null;

        ServiceConfigs serviceConfigs = utilityPlane.getExecutorServices().get(adapterName);
        if(serviceConfigs != null && serviceConfigs.getActive()) {
             executorClient = new ExecutorClient(serviceConfigs.getAddress());
        }
       
        return executorClient;
    }    
    

    @Data
    public static class ProductPlaneConfigs {
        ServiceConfigs registryService;
        ServiceConfigs devopsService;
        ServiceConfigs blueprintService;
    }

    @Data
    public static class UtiltyPlaneConfigs {
        Map<String, ServiceConfigs> policyServices;
        Map<String, ServiceConfigs> executorServices;
        Map<String, ServiceConfigs> notificationServices;
    }
   
    @Data
    public static class ServiceConfigs {
        Boolean active;
        String address;
    }
}
