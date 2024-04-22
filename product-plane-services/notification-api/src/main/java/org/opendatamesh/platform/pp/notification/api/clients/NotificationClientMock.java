package org.opendatamesh.platform.pp.notification.api.clients;

import org.opendatamesh.platform.pp.notification.api.resources.*;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Random;

public class NotificationClientMock implements NotificationClient {

    private Random random = new Random();

    public NotificationClientMock() {

    }

    public ObserverResource addObserver(ObserverResource observerResource) {
        observerResource.setId(random.nextLong());
        return observerResource;
    }

    public ObserverResource updateObserver(Long id, ObserverResource observerResource) {
        return observerResource;
    }

    public Page<ObserverResource> getObservers(Pageable pageable, ObserverSearchOptions searchOptions) {
        return null;
    }

    public ObserverResource getObserver(Long id) {
        return null;
    }

    public void removeObserver(Long id) {

    }

    public void notifyEvent(EventResource eventResource) {

    }

    public EventNotificationResource createNotification(EventNotificationResource eventNotificationResource) {
        eventNotificationResource.setId(random.nextLong());
        return eventNotificationResource;
    }

    public EventNotificationResource updateNotification(Long notificationId, EventNotificationResource eventNotificationResource) {
        return eventNotificationResource;
    }

    public EventNotificationResource readOneNotification(Long notificationId) {
        return null;
    }

    @Override
    public Page<EventNotificationResource> searchNotifications(Pageable pageable, EventNotificationSearchOptions searchOption) {
        return null;
    }

    @Override
    public void deleteNotification(Long notificationId) {

    }
}
