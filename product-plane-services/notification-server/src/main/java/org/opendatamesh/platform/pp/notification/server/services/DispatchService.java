package org.opendatamesh.platform.pp.notification.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.api.resources.exceptions.EventNotifierApiStandardErrors;
import org.opendatamesh.platform.pp.notification.server.database.entities.Observer;
import org.opendatamesh.platform.pp.notification.server.services.proxies.EventNotifierNotificationServiceProxy;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
public class DispatchService {

    @Autowired
    ObserverService observerService;

    @Autowired
    EventNotifierNotificationServiceProxy eventNotifierNotificationServiceProxy;

    public void notifyAll(EventResource eventToDispatch) {
        if(eventToDispatch == null) {
            throw new BadRequestException(
                    EventNotifierApiStandardErrors.SC400_02_EVENT_IS_EMPTY,
                    "Event object cannot be null"
            );
        }
        Slice<Observer> observerSlice = observerService.findAll(PageRequest.of(0, 20));
        notifyAllSlice(observerSlice, eventToDispatch);
        while(observerSlice.hasNext()) {
            observerSlice = observerService.findAll(observerSlice.nextPageable());
            notifyAllSlice(observerSlice, eventToDispatch);
        }
    }

    private void notifyAllSlice(Slice<Observer> observerSlice, EventResource eventToDispatch) {
        observerSlice.getContent().forEach(observer -> notifyOne(observer.getObserverServerBaseUrl(), eventToDispatch));
    }

    private void notifyOne(String observerBaseUrl, EventResource eventToDispatch) {
        eventNotifierNotificationServiceProxy.postEventToNotificationService(eventToDispatch, observerBaseUrl);
    }

}