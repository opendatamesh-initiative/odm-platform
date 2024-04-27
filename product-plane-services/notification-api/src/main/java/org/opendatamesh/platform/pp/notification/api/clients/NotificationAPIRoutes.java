package org.opendatamesh.platform.pp.notification.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum NotificationAPIRoutes implements ODMApiRoutes {

    OBSERVERS("/observers"),
    EVENTS("/events"),
    NOTIFICATIONS("/notifications"),
    DISPATCH("/dispatch");

    private final String path;

    private static final String CONTEXT_PATH = "/api/v1/pp/event-notifier";

    NotificationAPIRoutes(String path) {
        this.path = CONTEXT_PATH + path;
    }

    @Override
    public String getPath() {
        return path;
    }

}
