package org.opendatamesh.platform.pp.devops.api.clients;

import org.opendatamesh.platform.core.commons.clients.RoutesInterface;

public enum Routes implements RoutesInterface {

    ACTIVITIES("/api/v1/pp/devops/activities"),
    TASKS("/api/v1/pp/devops/tasks");

    private final String path;

    Routes(String path) {
        this.path = path;
    }

    @Override
    public String toString() {
        return this.path;
    }

    public String getPath() {
        return path;
    }

}
