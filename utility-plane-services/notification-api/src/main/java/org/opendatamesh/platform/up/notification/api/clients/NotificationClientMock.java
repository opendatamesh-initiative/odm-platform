package org.opendatamesh.platform.up.notification.api.clients;

import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.opendatamesh.platform.up.notification.api.resources.NotificationStatus;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

public class NotificationClientMock implements NotificationClient{
    
    private Random random;

    private List<NotificationResource> notificationResourceList;

    public NotificationClientMock() {
        this.random = new Random();
        this.notificationResourceList = new ArrayList<>();
    }

    @Override
    public NotificationResource createNotification(NotificationResource notificationRes) {
        notificationRes.setId(random.nextLong());
        notificationRes.setStatus(NotificationStatus.PROCESSED);
        notificationRes.setReceivedAt(new Date(System.currentTimeMillis()));
        notificationRes.setProcessedAt(new Date(System.currentTimeMillis()));
        notificationResourceList.add(notificationRes);
        return notificationRes;
    }

    @Override
    public NotificationResource readOneNotification(Long notificationId) {
        Optional<NotificationResource> notificationResource = notificationResourceList.stream()
                .filter(notification -> notification.getId() == notificationId)
                .findFirst();
        if(notificationResource.isPresent())
            return notificationResource.get();
        return null;
    }

    @Override
    public List<NotificationResource> searchNotifications(String eventType, String notificationStatus) {
        return notificationResourceList.stream()
                .filter(notification ->
                        (eventType == null || notification.getEvent().getType().equals(eventType)) &&
                        (notificationStatus == null || notification.getStatus().equals(notificationStatus)))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteNotification(Long notificationId) {
        notificationResourceList.removeIf(notification -> notification.getId() == notificationId);
    }

}
