package org.opendatamesh.platform.pp.event.notifier.api.controllers;

import org.opendatamesh.platform.up.notification.api.resources.EventResource;

public interface DispatchController {

    void notifyEvent(EventResource eventResource);

}