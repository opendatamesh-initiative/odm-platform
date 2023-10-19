package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.opendatamesh.platform.pp.blueprint.server.clients.github.GitHubClient;

public class GitHubService extends GitService {

    private GitHubClient gitHubClient;

    public GitHubService() {
        this.gitHubClient = new GitHubClient();
    }

    @Override
    public void createRepo(String repositoryName) {
        gitHubClient.createRemoteRepository(repositoryName);
    }

}
