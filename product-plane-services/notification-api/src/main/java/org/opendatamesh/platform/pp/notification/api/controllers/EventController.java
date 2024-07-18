package org.opendatamesh.platform.pp.notification.api.controllers;

import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventSearchOptions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventController {

    EventResource readOneEvent(Long eventId);

    Page<EventResource> searchEvents(Pageable pageable, EventSearchOptions searchOption);

}
