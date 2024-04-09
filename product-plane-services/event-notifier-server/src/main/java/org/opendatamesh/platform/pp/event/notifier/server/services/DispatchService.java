package org.opendatamesh.platform.pp.event.notifier.server.services;

import org.opendatamesh.platform.pp.event.notifier.server.database.entities.Observer;
import org.opendatamesh.platform.pp.event.notifier.server.services.proxies.EventNotifierNotificationServiceProxy;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DispatchService {

    @Autowired
    ObserverService observerService;

    public void notifyAll(EventResource eventToDispatch) {
        List<Observer> observers = observerService.findAll(Pageable.unpaged()).getContent();
        for (Observer observer : observers) {
            EventNotifierNotificationServiceProxy.postEventToNotificationService(
                    eventToDispatch,
                    observer.getObserverServerAddress()
            );
        }
    }

}
