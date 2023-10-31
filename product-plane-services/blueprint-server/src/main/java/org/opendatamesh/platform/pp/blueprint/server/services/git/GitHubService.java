package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.opendatamesh.platform.pp.blueprint.server.clients.github.GitHubClient;
import org.opendatamesh.platform.pp.blueprint.server.components.OAuthTokenManager;

public class GitHubService extends GitService {

    private GitHubClient gitHubClient;

    public GitHubService(String personalAccessToken) {
        this.gitHubClient = new GitHubClient(personalAccessToken);
    }

    @Override
    public void createRepo(String organization, String projectName, String repositoryName) {
        gitHubClient.createRemoteRepository(organization, repositoryName);
    }

}
