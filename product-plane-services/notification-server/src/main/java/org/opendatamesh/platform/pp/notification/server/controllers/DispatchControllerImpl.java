package org.opendatamesh.platform.pp.notification.server.controllers;

import org.opendatamesh.platform.pp.notification.api.controllers.AbstractDispatchController;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.server.services.DispatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DispatchControllerImpl extends AbstractDispatchController {

    @Autowired
    DispatchService dispatchService;

    @Override
    public void notifyEvent(EventResource eventResource) {
        dispatchService.notifyAll(eventResource);
    }

}
