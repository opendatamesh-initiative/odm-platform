package org.opendatamesh.platform.pp.notification.server.controllers;

import org.opendatamesh.platform.pp.notification.api.controllers.AbstractEventNotificationController;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationSearchOptions;
import org.opendatamesh.platform.pp.notification.server.services.EventNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EventNotificationControllerImpl extends AbstractEventNotificationController {

    @Autowired
    EventNotificationService eventNotificationService;

    /*@Override
    public EventNotificationResource createNotification(EventNotificationResource notificationRes) {
        return eventNotificationService.createResource(notificationRes);
    }*/

    @Override
    public EventNotificationResource updateEventNotification(Long id, EventNotificationResource eventNotificationResource) {
        return eventNotificationService.overwriteResource(id, eventNotificationResource);
    }

    @Override
    public EventNotificationResource readOneEventNotification(Long notificationId) {
        return eventNotificationService.findOneResource(notificationId);
    }

    @Override
    public Page<EventNotificationResource> searchEventNotifications(Pageable pageable, EventNotificationSearchOptions searchOptions) {
        return eventNotificationService.findAllResourcesFiltered(pageable, searchOptions);
    }

    /*@Override
    public void deleteNotification(Long notificationId) {
        eventNotificationService.delete(notificationId);
    }*/

}
