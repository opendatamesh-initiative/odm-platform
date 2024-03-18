package org.opendatamesh.platform.pp.event.notifier.api.mock.server.observers;

import org.opendatamesh.platform.pp.event.notifier.api.mock.server.Observer;
import org.opendatamesh.platform.up.notification.api.resources.EventResource;
import org.springframework.beans.factory.annotation.Autowired;

public class NotificationServiceObserver implements Observer {

    @Autowired
    private NotificationServiceProxy notificationServiceProxy;

    public NotificationServiceObserver(String serverAddress) {
        this.notificationServiceProxy = new NotificationServiceProxy(serverAddress);
    }

    @Override
    public void notify(EventResource event) {
        notificationServiceProxy.postEventToNotifcationService(event);
    }

}
