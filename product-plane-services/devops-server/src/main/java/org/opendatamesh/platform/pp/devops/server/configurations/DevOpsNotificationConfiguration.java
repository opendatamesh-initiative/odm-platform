package org.opendatamesh.platform.pp.devops.server.configurations;

import org.opendatamesh.platform.pp.notification.api.clients.DispatchClient;
import org.opendatamesh.platform.pp.notification.api.clients.NotificationClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DevOpsNotificationConfiguration {

    @Value("${odm.productPlane.notificationService.active}")
    private Boolean notificationActive;

    @Value("${odm.productPlane.notificationService.address}")
    private String notificationServerAddress;

    private static final Logger logger = LoggerFactory.getLogger(DevOpsNotificationConfiguration.class);

    @Bean
    public DispatchClient notificationClient() {
        DispatchClient notificationClient = null;
        if(!notificationActive) {
            logger.debug("Skipping notification service");
        } else {
            notificationClient = new NotificationClientImpl(notificationServerAddress);
        }
        return notificationClient;
    }

}