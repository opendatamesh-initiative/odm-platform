package org.opendatamesh.platform.pp.devops.server.configurations;

import org.opendatamesh.platform.pp.registry.api.v1.clients.RegistryClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class DevOpsConfigurations {

    @Value("${odm.utility-plane.executor-services.azure-devops.address}")
    String registryAddress;

    @Bean
    public RegistryClient getRegistryClient() {
        return new RegistryClient(registryAddress);
    }
}
