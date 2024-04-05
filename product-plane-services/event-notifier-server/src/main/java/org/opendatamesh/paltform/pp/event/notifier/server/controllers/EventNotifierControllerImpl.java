package org.opendatamesh.paltform.pp.event.notifier.server.controllers;

import org.opendatamesh.paltform.pp.event.notifier.server.services.ListenerService;
import org.opendatamesh.platform.pp.event.notifier.api.controllers.AbstractEventNotifierController;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ListenerResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventNotifierControllerImpl extends AbstractEventNotifierController {

    @Autowired
    ListenerService listenerService;

    @Override
    public ListenerResource addListener(ListenerResource listenerResource) {
        return listenerService.createResource(listenerResource);
    }

    @Override
    public ListenerResource updateListener(Long listenerId, ListenerResource listenerResource) {
        return listenerService.overwriteResource(listenerId, listenerResource);
    }

    @Override
    public void removeListener(Long id) {
        listenerService.delete(id);
    }

    @Override
    public void notifyEvent(EventResource eventResource) {
        listenerService.notifyAll(eventResource);
    }

}
