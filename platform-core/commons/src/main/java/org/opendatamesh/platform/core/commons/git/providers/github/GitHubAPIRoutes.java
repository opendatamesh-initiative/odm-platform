package org.opendatamesh.platform.core.commons.git.providers.github;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum GitHubAPIRoutes implements ODMApiRoutes {

    GITHUB_API_REPOS("/user/repos");

    private final String path;

    GitHubAPIRoutes(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return this.path;
    }

    @Override
    public String getPath() {
        return path;
    }

}
