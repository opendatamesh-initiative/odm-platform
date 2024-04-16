package org.opendatamesh.platform.pp.devops.server.configurations;

import org.opendatamesh.platform.pp.event.notifier.api.clients.EventNotifierClient;
import org.opendatamesh.platform.pp.event.notifier.api.clients.EventNotifierClientMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Configuration
public class DevOpsEventNotifierConfigurationTest {

    @Value("${odm.utilityPlane.notificationServices.blindata.active}")
    private Boolean notificationServiceActive;

    @Value("${odm.utilityPlane.notificationServices.blindata.address}")
    private String notificationServiceServerAddress;

    @Value("${odm.productPlane.eventNotifierService.active}")
    private Boolean eventNotifierServiceActive;

    @Value("${odm.productPlane.eventNotifierService.address}")
    private String eventNotifierServiceServerAddress;

    private static final Logger logger = LoggerFactory.getLogger(DevOpsEventNotifierConfigurationTest.class);

    @Bean
    @Profile({"test", "testpostgresql", "testmysql"})
    @Primary
    public EventNotifierClient mockEventNotifierClient() {
        EventNotifierClient eventNotifierClient = new EventNotifierClientMock();
        if(eventNotifierServiceActive)
            logger.debug("Skipping notification service");
        return eventNotifierClient;
    }

}
