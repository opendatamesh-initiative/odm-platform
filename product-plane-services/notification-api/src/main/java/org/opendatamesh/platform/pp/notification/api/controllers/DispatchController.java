package org.opendatamesh.platform.pp.notification.api.controllers;

import org.opendatamesh.platform.pp.notification.api.resources.v1.EventResource;

public interface DispatchController {

    void notifyEvent(EventResource eventResource);

}