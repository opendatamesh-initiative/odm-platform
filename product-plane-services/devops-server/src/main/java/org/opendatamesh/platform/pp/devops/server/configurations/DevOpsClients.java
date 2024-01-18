package org.opendatamesh.platform.pp.devops.server.configurations;

import lombok.Data;
import org.opendatamesh.platform.pp.devops.server.configurations.DevOpsConfigurations.ServiceConfigs;
import org.opendatamesh.platform.pp.registry.api.clients.RegistryClient;
import org.opendatamesh.platform.up.executor.api.clients.ExecutorClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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
            DevOpsConfigurations.ExecutorServicesConfigs executorServicesConfigs =
                    configs.getUtilityPlane().getExecutorServices().get(adapterName);
            if(executorServicesConfigs != null && executorServicesConfigs.getActive()) {
                executorsClients.put(adapterName, new ExecutorClient(
                        executorServicesConfigs.getAddress(),
                        executorServicesConfigs.getCheckAfterCallback()
                ));
            }
        }
    }

    public ExecutorClient getExecutorClient(String adapterName) {
        return executorsClients.get(adapterName);
    }

}
