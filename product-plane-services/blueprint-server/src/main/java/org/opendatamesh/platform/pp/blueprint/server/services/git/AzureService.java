package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.opendatamesh.platform.core.commons.oauth.OAuthTokenManager;
import org.opendatamesh.platform.pp.blueprint.server.clients.azure.AzureDevOpsClient;

public class AzureService extends GitService {

    private AzureDevOpsClient azureDevOpsClient;

    public AzureService(OAuthTokenManager oAuthTokenManager) {
        super(oAuthTokenManager);
        this.azureDevOpsClient = new AzureDevOpsClient(oAuthTokenManager);
    }

    @Override
    public void createRepo(String organization, String projectId, String repositoryName) {
        azureDevOpsClient.createRemoteRepository(organization, projectId, repositoryName);
    }

}
