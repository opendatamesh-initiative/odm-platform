package org.opendatamesh.platform.pp.registry.resources.v1.observers;

import org.opendatamesh.notification.EventResource;

public interface Observer {
    public void notify(EventResource event);
}
