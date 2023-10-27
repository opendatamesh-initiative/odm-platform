package org.opendatamesh.platform.pp.blueprint.server.clients.azure;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum AzureDevOpsAPIRoutes implements ODMApiRoutes {

    AZURE_DEVOPS_API_REPOS("/{organization}/{project}/_apis/git/repositories"); //?sourceRef={sourceRef}&api-version=5.0");

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
