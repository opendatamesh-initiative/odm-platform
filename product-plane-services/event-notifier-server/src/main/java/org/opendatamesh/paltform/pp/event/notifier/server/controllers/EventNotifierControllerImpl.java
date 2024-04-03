package org.opendatamesh.paltform.pp.event.notifier.server.controllers;

import org.opendatamesh.platform.pp.event.notifier.api.controllers.AbstractEventNotifierController;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ListenerResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventNotifierControllerImpl extends AbstractEventNotifierController {
    @Override
    public ListenerResource addListener(ListenerResource listenerResource) {
        return null;
    }

    @Override
    public void removeListener(Long id) {

    }

    @Override
    public void notifyEvent(EventResource eventResource) {

    }

}
