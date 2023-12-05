package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.opendatamesh.platform.pp.blueprint.server.clients.azure.AzureDevOpsClient;
import org.opendatamesh.platform.pp.blueprint.server.components.OAuthTokenManager;

public class AzureService extends GitService {

    private AzureDevOpsClient azureDevOpsClient;

    public AzureService(OAuthTokenManager oAuthTokenManager) {
        this.azureDevOpsClient = new AzureDevOpsClient(oAuthTokenManager);
    }

    @Override
    public void createRepo(String organization, String projectId, String repositoryName) {
        azureDevOpsClient.createRemoteRepository(organization, projectId, repositoryName);
    }

}
