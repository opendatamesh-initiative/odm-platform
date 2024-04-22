package org.opendatamesh.platform.pp.devops.server.configurations;

import org.opendatamesh.platform.pp.notification.api.clients.NotificationClient;
import org.opendatamesh.platform.pp.notification.api.clients.NotificationClientMock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration
@Profile({"test", "testpostgresql", "testmysql"})
public class DevOpsTestConfiguration {

    @Value("${odm.productPlane.eventNotifierService.active}")
    private Boolean eventNotifierServiceActive;

    private static final Logger logger = LoggerFactory.getLogger(TestConfiguration.class);

    @Bean
    @Primary
    public NotificationClient eventNotifierClient() {
        NotificationClient eventNotifierClient = new NotificationClientMock();
        if(eventNotifierServiceActive)
            logger.debug("Skipping notification service");
        return eventNotifierClient;
    }

}
