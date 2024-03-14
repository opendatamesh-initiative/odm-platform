package org.opendatamesh.platform.pp.event.notifier.api.mock.server;

import org.opendatamesh.platform.pp.event.notifier.api.controllers.EventNotifierController;
import org.opendatamesh.platform.pp.event.notifier.api.mock.server.observers.NotificationServiceObserver;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ListenerResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;

public class EventNotifierMockServer implements EventNotifierController {

    private EventNotifier eventNotifier;

    public EventNotifierMockServer(EventNotifier eventNotifier) {
        this.eventNotifier = eventNotifier;
    }

    @Override
    public ListenerResource addListener(ListenerResource listenerResource) {
        NotificationServiceObserver notificationServiceObserver = new NotificationServiceObserver(
                listenerResource.getListenerServerAddress()
        );
        eventNotifier.addObserver(notificationServiceObserver);
        return null;
    }

    @Override
    public void removeListener(Long id) {
        // Not supported in Mock version
    }

    @Override
    public void notifyEvent(EventResource eventResource) {
        eventNotifier.notifyEvent(eventResource);
    }

}
