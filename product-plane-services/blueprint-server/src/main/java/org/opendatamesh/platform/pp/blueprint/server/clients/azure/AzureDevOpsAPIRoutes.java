package org.opendatamesh.platform.pp.blueprint.server.clients.azure;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum AzureDevOpsAPIRoutes implements ODMApiRoutes {

    AZURE_DEVOPS_API_REPOS("/user/repos");

    private final String path;

    AzureDevOpsAPIRoutes(String path) {
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
