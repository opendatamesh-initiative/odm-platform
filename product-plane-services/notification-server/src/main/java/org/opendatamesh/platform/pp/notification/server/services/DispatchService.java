package org.opendatamesh.platform.pp.notification.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventNotificationStatus;
import org.opendatamesh.platform.pp.notification.api.resources.exceptions.NotificationApiStandardErrors;
import org.opendatamesh.platform.pp.notification.server.services.proxies.NotificationObserverClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
public class DispatchService {

    @Autowired
    private ObserverService observerService;
    @Autowired
    private EventNotificationService eventNotificationService;
    @Autowired
    private EventService eventService;
    @Autowired
    private NotificationObserverClient observerClient;

    public void notifyAll(EventResource eventToDispatch) {
        if (eventToDispatch == null) {
            throw new BadRequestException(
                    NotificationApiStandardErrors.SC400_02_EVENT_IS_EMPTY,
                    "Event object cannot be null"
            );
        }
        // Create Event
        eventToDispatch = eventService.createResource(eventToDispatch);
        // Find all observers (page by page) and forward the event
        Slice<ObserverResource> observerSlice = observerService.findAllResources(PageRequest.of(0, 20));
        notifyAllSlice(observerSlice, eventToDispatch);
        while (observerSlice.hasNext()) {
            observerSlice = observerService.findAllResources(observerSlice.nextPageable());
            notifyAllSlice(observerSlice, eventToDispatch);
        }
    }

    private void notifyAllSlice(Slice<ObserverResource> observerSlice, EventResource eventToDispatch) {
        observerSlice.getContent().forEach(observer -> notifyOne(observer, eventToDispatch));
    }

    private void notifyOne(ObserverResource observer, EventResource eventToDispatch) {
        // Init the notification
        EventNotificationResource eventNotificationResource = initEventNotification(observer, eventToDispatch);
        // Create EventNotification
        eventNotificationResource = eventNotificationService.createResource(eventNotificationResource);
        // Dispatch EventNotification to observer
        observerClient.dispatchEventNotificationToObserver(eventNotificationResource, observer);
    }

    private EventNotificationResource initEventNotification(ObserverResource observer, EventResource event) {
        EventNotificationResource eventNotificationResource = new EventNotificationResource();
        eventNotificationResource.setEvent(event);
        eventNotificationResource.setObserver(observer);
        eventNotificationResource.setStatus(EventNotificationStatus.PROCESSING);
        return eventNotificationResource;
    }

}