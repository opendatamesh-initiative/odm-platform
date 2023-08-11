package org.opendatamesh.platform.pp.devops.server.configurations;

import java.util.HashMap;
import java.util.Map;

import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsConfigurations.ServiceConfigs;
import org.opendatamesh.platform.pp.registry.api.v1.clients.RegistryClient;
import org.opendatamesh.platform.up.executor.api.clients.ExecutorClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;

@Component
@Data
public class DevOpsClients {

    DevOpsConfigurations configs;

    RegistryClient registryClient;
    Map<String, ExecutorClient> executorsClients; 

    @Autowired
    public DevOpsClients(DevOpsConfigurations configs) {
        this.configs = configs;
        registryClient = new RegistryClient(configs.getProductPlane().getRegistryService().getAddress());
        executorsClients = new HashMap<String, ExecutorClient>(); 
        for(String adapterName : configs.getUtilityPlane().getExecutorServices().keySet()) {
            ServiceConfigs serviceConfigs = configs.getUtilityPlane().getExecutorServices().get(adapterName);
            if(serviceConfigs != null && serviceConfigs.getActive()) {
                executorsClients.put(adapterName, new ExecutorClient(serviceConfigs.getAddress()));
            }
        }
    }

    public ExecutorClient getExecutorClient(String adapterName) {
        return executorsClients.get(adapterName);
    }    
}
