package org.opendatamesh.platform.up.notification.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum NotificationAPIRoutes implements ODMApiRoutes {

    METASERVICE_NOTIFICATION("/api/v1/up/metaservice/notifications");

    private final String path;

    NotificationAPIRoutes(String path) {
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
