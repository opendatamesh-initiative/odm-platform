package org.opendatamesh.platform.pp.registry.server.configurations;

import org.opendatamesh.platform.pp.notification.api.clients.NotificationClient;
import org.opendatamesh.platform.pp.notification.api.clients.NotificationClientMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@TestConfiguration
@Profile({"test", "testpostgresql", "testmysql"})
public class RegistryTestConfiguration {

    @Value("${odm.productPlane.eventNotifierService.active}")
    private Boolean eventNotifierServiceActive;

    private static final Logger logger = LoggerFactory.getLogger(RegistryEventNotifierConfiguration.class);

    @Bean
    @Primary
    public NotificationClient eventNotifierClient() {
        NotificationClient eventNotifierClient = new NotificationClientMock();
        if(eventNotifierServiceActive)
            logger.debug("Skipping notification service");
        return eventNotifierClient;
    }

}
