package org.opendatamesh.platform.pp.notification.api.controllers;

import org.opendatamesh.platform.pp.notification.api.resources.EventResource;

public interface DispatchController {

    void notifyEvent(EventResource eventResource);

}