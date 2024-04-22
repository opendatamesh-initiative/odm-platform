package org.opendatamesh.platform.pp.notification.server.services;

import org.opendatamesh.platform.core.commons.servers.exceptions.BadRequestException;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.exceptions.NotificationApiStandardErrors;
import org.opendatamesh.platform.pp.notification.server.database.entities.Observer;
import org.opendatamesh.platform.pp.notification.server.services.proxies.NotificationObserverServiceProxy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
public class DispatchService {

    @Autowired
    ObserverService observerService;

    @Autowired
    EventNotificationService eventNotificationService;

    @Autowired
    NotificationObserverServiceProxy notificationObserverServiceProxy;

    public void notifyAll(EventResource eventToDispatch) {
        if(eventToDispatch == null) {
            throw new BadRequestException(
                    NotificationApiStandardErrors.SC400_02_EVENT_IS_EMPTY,
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
        /*
        * Qui basicamente cambia tutto il flusso:
        * - questo modulo salva le notifiche, non più l'observer
        * - l'observer riceve la notifica, la gestisce e poi chiamerà questo modulo per aggiornare la notifica
        * TODO:
        *  - rinomina proxy e moduli di UP come observer e non più notification
        *  - usa notification service per creare la notifica
        *  - aggiorna gli observer in modo da avere solo un metodo per ricevere una notifica e gestirla
        *  - aggiorna gli observer per aggiornare a DB la notifica dopo averla processata
        *  - valuta se duplicare le Notifications, una per ogni observer a cui è stata mandata, o se creare una tabella di log
        * */
        //notificationService.createNotification()
        //eventNotifierNotificationServiceProxy.sendNotification()
        notificationObserverServiceProxy.postEventToNotificationService(eventToDispatch, observerBaseUrl);

        // NEW
        EventNotificationResource eventNotificationResource = new EventNotificationResource();
        eventNotificationResource.setEvent(eventToDispatch);
    }

}