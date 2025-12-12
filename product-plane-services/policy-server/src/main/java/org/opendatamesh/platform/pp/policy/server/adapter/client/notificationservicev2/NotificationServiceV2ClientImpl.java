package org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2;

import org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.resources.NotificationV2Res;
import org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.resources.NotificationV2SubscribeRequestRes;
import org.opendatamesh.platform.pp.policy.server.client.utils.RestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class NotificationServiceV2ClientImpl implements NotificationServiceV2Client {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    // Notification service endpoints
    private static final String HEALTH_ENDPOINT = "/actuator/health";
    private static final String SUBSCRIBE_ENDPOINT = "/api/v2/pp/notification/subscriptions/subscribe";
    private static final String EMIT_EVENT_ENDPOINT = "/api/v2/pp/notification/events/emit";
    private static final String NOTIFICATION_ENDPOINT = "/api/v2/pp/notification/notifications";

    // Observer configuration
    private final String baseUrl;
    private final String observerName;
    private final String observerDisplayName;
    private static final String OBSERVER_API_VERSION = "V2"; // Always v2

    private final RestUtils restUtils;
    private final String notificationServiceBaseUrl;

    NotificationServiceV2ClientImpl(String baseUrl, String observerName, String observerDisplayName, String notificationServiceBaseUrl, RestUtils restUtils) {
        this.baseUrl = baseUrl;
        this.observerName = observerName;
        this.observerDisplayName = observerDisplayName;
        this.notificationServiceBaseUrl = notificationServiceBaseUrl;
        this.restUtils = restUtils;
    }

    @Override
    public void assertConnection() {
        restUtils.genericGet(String.format("%s%s", notificationServiceBaseUrl, HEALTH_ENDPOINT), null, null, Object.class);
    }

    @Override
    public void subscribeToEvents(List<String> eventTypes) {
        NotificationV2SubscribeRequestRes req = createSubscribeRequest(eventTypes);
        restUtils.genericPost(String.format("%s%s", notificationServiceBaseUrl, SUBSCRIBE_ENDPOINT), null, req, Object.class);
        log.info("Subscribed to events: {}", req.getEventTypes());
    }

    @Override
    public void notifyEvent(Object event) {
        EventEmitCommandRes req = new EventEmitCommandRes(event);
        restUtils.genericPost(
                String.format("%s%s", notificationServiceBaseUrl, EMIT_EVENT_ENDPOINT),
                null,
                req,
                Object.class
        );
    }

    @Override
    public void processingSuccess(Long notificationId) {
        NotificationV2Res notification = getNotification(notificationId);
        notification.setStatus(NotificationV2Res.NotificationV2StatusRes.PROCESSED);
        restUtils.put(String.format("%s%s/{id}", notificationServiceBaseUrl, NOTIFICATION_ENDPOINT), null, notificationId, notification, NotificationV2Res.class);
    }

    @Override
    public void processingFailure(Long notificationId) {
        NotificationV2Res notification = getNotification(notificationId);
        notification.setStatus(NotificationV2Res.NotificationV2StatusRes.FAILED_TO_PROCESS);
        restUtils.put(String.format("%s%s/{id}", notificationServiceBaseUrl, NOTIFICATION_ENDPOINT), null, notificationId, notification, NotificationV2Res.class);
    }

    private NotificationV2Res getNotification(Long notificationId) {
        return restUtils.get(String.format("%s%s/{id}", notificationServiceBaseUrl, NOTIFICATION_ENDPOINT), null, notificationId, NotificationV2Res.class);
    }

    private NotificationV2SubscribeRequestRes createSubscribeRequest(List<String> eventTypes) {
        NotificationV2SubscribeRequestRes req = new NotificationV2SubscribeRequestRes();
        req.setObserverBaseUrl(baseUrl);
        req.setName(observerName);
        req.setDisplayName(observerDisplayName);
        req.setObserverApiVersion(OBSERVER_API_VERSION);
        req.setEventTypes(eventTypes);
        return req;
    }

    public class EventEmitCommandRes {
        Object event;

        public EventEmitCommandRes(Object event) {
            this.event = event;
        }

        public Object getEvent() {
            return event;
        }

        public void setEvent(Object event) {
            this.event = event;
        }

    }
}