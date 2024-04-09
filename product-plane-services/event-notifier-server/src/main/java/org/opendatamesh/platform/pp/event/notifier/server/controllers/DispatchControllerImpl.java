package org.opendatamesh.platform.pp.event.notifier.server.controllers;

import org.opendatamesh.platform.pp.event.notifier.server.services.DispatchService;
import org.opendatamesh.platform.pp.event.notifier.api.controllers.DispatchController;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DispatchControllerImpl implements DispatchController {

    @Autowired
    DispatchService dispatchService;

    @Override
    public void notifyEvent(EventResource eventResource) {
        dispatchService.notifyAll(eventResource);
    }

}
