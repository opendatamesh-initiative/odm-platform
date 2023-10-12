package org.opendatamesh.platform.pp.blueprint.server.services.git;

import org.opendatamesh.platform.pp.blueprint.api.resources.RepositoryProviderEnum;
import org.springframework.stereotype.Component;

@Component
public class GitHubService implements GitService {
    @Override
    public RepositoryProviderEnum getType() {
        return RepositoryProviderEnum.GITHUB;
    }

    @Override
    public void createRepo() {

    }
}
