package org.opendatamesh.platform.up.notification.api.clients;

import org.opendatamesh.platform.core.commons.clients.RoutesInterface;

public enum NotificationAPIRoutes implements RoutesInterface {

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
