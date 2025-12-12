package org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2;

import org.opendatamesh.platform.pp.policy.server.client.utils.RestUtilsFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Configuration
public class NotificationServiceV2ClientConfig {
    private static final Logger logger = LoggerFactory.getLogger(NotificationServiceV2ClientConfig.class);

    @Value("${server.baseUrl}")
    private String baseUrl;

    @Value("${odm.policy.adapter.observer.name:policyAdapter}")
    private String observerName;

    @Value("${registry.observer.displayName:policy}")
    private String observerDisplayName;

    @Value("${odm.productPlane.notificationService.address}")
    private String notificationServiceBaseUrl;

    @Value("${odm.productPlane.notificationService.active}")
    private boolean notificationServiceActive;

    @Bean
    public NotificationServiceV2Client notificationClient() {
        // Hardcoded event types that the Policy Service subscribes to
        if (notificationServiceActive) {
            return new NotificationServiceV2ClientImpl(baseUrl, observerName, observerDisplayName, notificationServiceBaseUrl, RestUtilsFactory.getRestUtils(new RestTemplate()));
        }
        // Notification service is not active, return a dummy implementation that does nothing
        logger.warn("Notification service is not active. Events will not be sent.");
        return createDummyNotificationClient();
    }

    private NotificationServiceV2Client createDummyNotificationClient() {
        return new NotificationServiceV2Client() {
            @Override
            public void assertConnection() {
                logger.warn("Notification service is not active. Connection not checked.");
            }

            @Override
            public void notifyEvent(Object event) {
                logger.warn("Notification service is not active. Event not sent: {}", event);
            }

            @Override
            public void subscribeToEvents(List<String> eventTypes) {
                logger.warn("Notification service is not active. Events not subscribed.");
            }

            @Override
            public void processingSuccess(Long notificationId) {
                logger.warn("Notification service is not active. Notification success not sent: {}", notificationId);
            }

            @Override
            public void processingFailure(Long notificationId) {
                logger.warn("Notification service is not active. Notification failure not sent: {}", notificationId);
            }
        };
    }

}
