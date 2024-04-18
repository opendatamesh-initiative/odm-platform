package org.opendatamesh.platform.pp.notification.api.clients;

import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverSearchOptions;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Random;

public class EventNotifierClientMock implements EventNotifierClient {

    private Random random = new Random();

    public EventNotifierClientMock() {

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

}
