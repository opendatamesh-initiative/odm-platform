package org.opendatamesh.platform.pp.registry.server.configurations;

import org.opendatamesh.platform.pp.notification.api.clients.EventNotifierClient;
import org.opendatamesh.platform.pp.notification.api.clients.EventNotifierClientImpl;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RegistryEventNotifierConfiguration {

    @Value("${odm.utilityPlane.notificationServices.blindata.active}")
    private Boolean notificationServiceActive;

    @Value("${odm.utilityPlane.notificationServices.blindata.address}")
    private String notificationServiceServerAddress;

    @Value("${odm.productPlane.eventNotifierService.active}")
    private Boolean eventNotifierServiceActive;

    @Value("${odm.productPlane.eventNotifierService.address}")
    private String eventNotifierServiceServerAddress;

    private static final Logger logger = LoggerFactory.getLogger(RegistryEventNotifierConfiguration.class);

    @Bean
    public EventNotifierClient eventNotifierClient() {
        EventNotifierClient eventNotifierClient = null;
        if(!eventNotifierServiceActive) {
            logger.debug("Skipping notification service");
        } else {
            eventNotifierClient = new EventNotifierClientImpl(eventNotifierServiceServerAddress);
            if(notificationServiceActive) {
                ObserverResource observerResource = new ObserverResource();
                observerResource.setName("blindata-observer");
                observerResource.setDisplayName("Blindata Observer");
                observerResource.setObserverServerBaseUrl(notificationServiceServerAddress);
                eventNotifierClient.addObserver(observerResource);
            }
        }
        return eventNotifierClient;
    }

}