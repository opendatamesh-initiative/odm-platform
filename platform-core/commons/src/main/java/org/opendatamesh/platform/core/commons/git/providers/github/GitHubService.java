package org.opendatamesh.platform.core.commons.git.providers.github;

import org.opendatamesh.platform.core.commons.git.GitService;

public class GitHubService extends GitService {

    private GitHubClient gitHubClient;

    public GitHubService(String personalAccessToken) {
        super(null);
        this.gitHubClient = new GitHubClient(personalAccessToken);
    }

    @Override
    public void createRepo(String organization, String projectName, String repositoryName) {
        gitHubClient.createRemoteRepository(organization, repositoryName);
    }

}
