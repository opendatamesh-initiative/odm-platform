package org.opendatamesh.platform.pp.devops.server.configurations;

import org.opendatamesh.platform.pp.event.notifier.api.clients.EventNotifierClient;
import org.opendatamesh.platform.pp.event.notifier.api.clients.EventNotifierClientMock;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ListenerResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventNotifierConfiguration {

    @Value("${odm.utilityPlane.notificationServices.blindata.active}")
    private Boolean notificationServiceActive;

    @Value("${odm.utilityPlane.notificationServices.blindata.address}")
    private String notificationServiceServerAddress;

    private static final Logger logger = LoggerFactory.getLogger(EventNotifierConfiguration.class);

    @Bean
    public EventNotifierClient eventNotifier() {

        // Configure EventNotifierClient
        EventNotifierClient eventNotifierClient = new EventNotifierClientMock();

        // Add observers
        if(!notificationServiceActive) {
            logger.debug("Skipping notification service");
        } else {
            ListenerResource listenerResource = new ListenerResource();
            listenerResource.setListenerServerAddress(notificationServiceServerAddress);
            eventNotifierClient.addListener(listenerResource);
        }

        return eventNotifierClient;
    }

}
