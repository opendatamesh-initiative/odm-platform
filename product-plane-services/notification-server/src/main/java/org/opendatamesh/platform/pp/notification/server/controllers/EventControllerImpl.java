package org.opendatamesh.platform.pp.notification.server.controllers;

import org.opendatamesh.platform.pp.notification.api.controllers.AbstractEventController;
import org.opendatamesh.platform.pp.notification.api.controllers.EventController;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventSearchOptions;
import org.opendatamesh.platform.pp.notification.server.services.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventControllerImpl extends AbstractEventController {

    @Autowired
    private EventService eventService;

    @Override
    public EventResource readOneEvent(Long eventId) {
        return eventService.findOneResource(eventId);
    }

    @Override
    public Page<EventResource> searchEvents(Pageable pageable, EventSearchOptions searchOption) {
        return eventService.findAllResourcesFiltered(pageable, searchOption);
    }

}
