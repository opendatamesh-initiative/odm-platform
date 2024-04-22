package org.opendatamesh.platform.up.observer.api.clients;

import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.enums.EventNotificationStatus;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ConsumeClientMock implements ConsumeClient {
    
    private Random random;

    private List<EventNotificationResource> eventNotificationResourceList;

    public ConsumeClientMock() {
        this.random = new Random();
        this.eventNotificationResourceList = new ArrayList<>();
    }

    @Override
    public void consumeEventNotification(EventNotificationResource notificationRes) {
        notificationRes.setId(random.nextLong());
        notificationRes.setStatus(EventNotificationStatus.PROCESSED);
        notificationRes.setReceivedAt(new Date(System.currentTimeMillis()));
        notificationRes.setProcessedAt(new Date(System.currentTimeMillis()));
    }

}
