package org.opendatamesh.platform.pp.blueprint.server.services;

import lombok.Data;
import org.opendatamesh.platform.pp.blueprint.server.services.git.AzureService;
import org.opendatamesh.platform.pp.blueprint.server.services.git.GitHubService;
import org.opendatamesh.platform.pp.blueprint.server.services.git.GitService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
@Data
public class GitServiceFactory {

    @Value("${git.provider}")
    private String serviceType;

    private GitService gitService;

    @PostConstruct
    public void initGitService() {
        switch (serviceType) {
            case "AZURE_DEVOPS":
                this.gitService = new AzureService();
                break;
            case "GITHUB":
                this.gitService = new GitHubService();
                break;
            default:
                throw new RuntimeException(
                        "Impossibile to initialize GitService - unknown Git Provider [" + serviceType + "]"
                );
        }
    }

}
