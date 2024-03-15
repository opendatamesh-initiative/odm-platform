package org.opendatamesh.platform.pp.event.notifier.api.controllers;

import org.opendatamesh.platform.pp.event.notifier.api.resources.ListenerResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;

public interface EventNotifierController {

    ListenerResource addListener(ListenerResource listenerResource);

    void removeListener(Long id);

    void notifyEvent(EventResource eventResource);

}
