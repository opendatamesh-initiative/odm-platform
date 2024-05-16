package org.opendatamesh.platform.pp.registry.server.configurations;

import org.opendatamesh.platform.pp.notification.api.clients.DispatchClient;
import org.opendatamesh.platform.pp.notification.api.clients.NotificationClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistryNotificationConfiguration {

    @Value("${odm.productPlane.notificationService.active}")
    private Boolean notificationServiceActive;

    @Value("${odm.productPlane.notificationService.address}")
    private String notificationServiceServerAddress;

    private static final Logger logger = LoggerFactory.getLogger(RegistryNotificationConfiguration.class);

    @Bean
    public DispatchClient notificationClient() {
        DispatchClient notificationClient = null;
        if(!notificationServiceActive) {
            logger.debug("Skipping notification service");
        } else {
            notificationClient = new NotificationClientImpl(notificationServiceServerAddress);
        }
        return notificationClient;
    }

}