package org.opendatamesh.platform.up.observer.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum ObserverAPIRoutes implements ODMApiRoutes {

    CONSUME("/api/v1/up/observer/notifications");

    private final String path;

    ObserverAPIRoutes(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

}
