package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.opendatamesh.platform.pp.blueprint.server.clients.azure.AzureDevOpsClient;

public class AzureService extends GitService {

    private AzureDevOpsClient azureDevOpsClient;

    public AzureService() {
        this.azureDevOpsClient = new AzureDevOpsClient();
    }

    @Override
    public void createRepo(String repositoryName) {
        azureDevOpsClient.createRemoteRepository(repositoryName);
    }

}
