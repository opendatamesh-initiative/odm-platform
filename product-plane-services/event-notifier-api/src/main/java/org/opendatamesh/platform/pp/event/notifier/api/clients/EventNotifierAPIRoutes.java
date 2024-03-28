package org.opendatamesh.platform.pp.event.notifier.api.clients;

import org.opendatamesh.platform.core.commons.clients.ODMApiRoutes;

public enum EventNotifierAPIRoutes implements ODMApiRoutes {

    EVENT_NOTIFIER("/notify");

    private final String path;

    private static final String CONTEXT_PATH = "/api/v1/pp/event-notifier";

    EventNotifierAPIRoutes(String path) {
        this.path = CONTEXT_PATH + path;
    }

    @Override
    public String getPath() {
        return path;
    }

}
