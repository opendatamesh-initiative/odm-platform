package org.opendatamesh.platform.pp.event.notifier.api.clients;

import org.opendatamesh.platform.pp.event.notifier.api.mock.server.EventNotifier;
import org.opendatamesh.platform.pp.event.notifier.api.mock.server.EventNotifierMockServer;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ListenerResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;

public class EventNotifierClientMock implements EventNotifierClient{

    private EventNotifierMockServer eventNotifierMockServer;

    public EventNotifierClientMock() {
        EventNotifier eventNotifier = new EventNotifier();
        this.eventNotifierMockServer = new EventNotifierMockServer(eventNotifier);
    }

    @Override
    public ListenerResource addListener(ListenerResource listenerResource) {
        eventNotifierMockServer.addListener(listenerResource);
        return null;
    }

    @Override
    public void removeListener(Long id) {
        eventNotifierMockServer.removeListener(id);
    }

    @Override
    public void notifyEvent(EventResource eventResource) {
        eventNotifierMockServer.notifyEvent(eventResource);
    }

}
