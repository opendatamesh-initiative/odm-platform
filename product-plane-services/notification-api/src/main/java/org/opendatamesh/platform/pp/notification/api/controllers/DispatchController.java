package org.opendatamesh.platform.pp.notification.api.controllers;

import org.opendatamesh.platform.up.notification.api.resources.EventResource;

public interface DispatchController {

    void notifyEvent(EventResource eventResource);

}