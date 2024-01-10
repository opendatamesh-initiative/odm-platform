package org.opendatamesh.platform.core.commons.git.providers.azuredevops;

import org.opendatamesh.platform.core.commons.git.GitService;
import org.opendatamesh.platform.core.commons.oauth.OAuthTokenManager;

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
