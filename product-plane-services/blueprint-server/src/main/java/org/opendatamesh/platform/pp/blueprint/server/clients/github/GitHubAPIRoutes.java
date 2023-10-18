package org.opendatamesh.platform.pp.blueprint.server.clients.github;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum GitHubAPIRoutes implements ODMApiRoutes {

    GITHUB_API_REPOSITORIES("/user/repos");

    private final String path;

    private static final String CONTEXT_PATH = "";

    GitHubAPIRoutes(String path) {
        this.path = CONTEXT_PATH + path;
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
