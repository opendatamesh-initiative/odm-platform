package org.opendatamesh.platform.pp.devops.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum DevOpsAPIRoutes implements ODMApiRoutes {

    ACTIVITIES("/activities"),
    TASKS("/tasks");

    private final String path;

    private static final String CONTEXT_PATH = "/api/v1/pp/devops";

    DevOpsAPIRoutes(String path) {
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
