package org.opendatamesh.platform.pp.event.notifier.api.mock.server;

import org.opendatamesh.platform.up.notification.api.resources.EventResource;

public interface Observer {
    public void notify(EventResource event);
}
