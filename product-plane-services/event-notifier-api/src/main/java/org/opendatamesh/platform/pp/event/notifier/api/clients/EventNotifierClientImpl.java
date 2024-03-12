package org.opendatamesh.platform.pp.event.notifier.api.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opendatamesh.platform.core.commons.clients.ODMClient;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ListenerResource;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;

public class EventNotifierClientImpl extends ODMClient implements EventNotifierClient {

    public EventNotifierClientImpl(String serverAddress) {
        super(serverAddress, ObjectMapperFactory.JSON_MAPPER);
    }

    public EventNotifierClientImpl(String serverAddress, ObjectMapper mapper) {
        super(serverAddress, mapper);
    }

    public ListenerResource addListener(ListenerResource listenerResource) {
        return null;
    }

    public void removeListener(Long id) {
    }

    public void notifyEvent(EventResource eventResource) {
    }

}
