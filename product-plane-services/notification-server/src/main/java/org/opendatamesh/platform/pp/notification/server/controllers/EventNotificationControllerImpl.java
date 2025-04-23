package org.opendatamesh.platform.pp.notification.server.controllers;

import org.opendatamesh.platform.pp.notification.api.controllers.AbstractEventNotificationController;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationSearchOptions;
import org.opendatamesh.platform.pp.notification.server.services.EventNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventNotificationControllerImpl extends AbstractEventNotificationController {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private EventNotificationService eventNotificationService;

    @Override
    public EventNotificationResource updateEventNotification(Long id, EventNotificationResource eventNotificationResource) {
        EventNotificationResource result =  eventNotificationService.overwriteResource(id, eventNotificationResource);
        log.info("Observer: {} notification on event: {} has status: {}", result.getObserver().getName(), result.getEvent().getType(), result.getStatus());
        return result;
    }

    @Override
    public EventNotificationResource readOneEventNotification(Long notificationId) {
        return eventNotificationService.findOneResource(notificationId);
    }

    @Override
    public Page<EventNotificationResource> searchEventNotifications(Pageable pageable, EventNotificationSearchOptions searchOptions) {
        return eventNotificationService.findAllResourcesFiltered(pageable, searchOptions);
    }

}
