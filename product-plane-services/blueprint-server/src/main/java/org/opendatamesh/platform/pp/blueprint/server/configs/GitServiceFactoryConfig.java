package org.opendatamesh.platform.pp.blueprint.server.configs;

import org.opendatamesh.platform.pp.blueprint.server.services.git.AzureService;
import org.opendatamesh.platform.pp.blueprint.server.services.git.GitHubService;
import org.opendatamesh.platform.pp.blueprint.server.services.git.GitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GitServiceFactoryConfig {

    @Value("${git.provider}")
    private String serviceType;

    @Bean
    public GitService gitService() {
        switch (serviceType) {
            case "AZURE_DEVOPS":
                return new AzureService();
            case "GITHUB":
                return new GitHubService();
            default:
                throw new RuntimeException(
                        "Impossibile to initialize GitService - unknown Git Provider [" + serviceType + "]"
                );
        }
    }

}
