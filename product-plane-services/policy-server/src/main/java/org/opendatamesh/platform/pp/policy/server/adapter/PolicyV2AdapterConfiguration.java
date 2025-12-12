package org.opendatamesh.platform.pp.policy.server.adapter;

import org.opendatamesh.platform.pp.policy.server.adapter.client.notificationservicev2.NotificationServiceV2Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class PolicyV2AdapterConfiguration {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Bean
    @ConditionalOnProperty(
            name = "odm.productPlane.policy.adapter.active",
            havingValue = "true"
    )
    public PolicyV2AdapterObserver policyV2AdapterObserver(NotificationServiceV2Client notificationClient) {
        logger.info("Checking connection to Notification V2 service: ...");
        notificationClient.assertConnection();
        logger.info("Checking connection to Notification V2 service: OK");
        notificationClient.subscribeToEvents(List.of(
                NotificationV2EventType.DATA_PRODUCT_INITIALIZATION_REQUESTED.getValue(),
                NotificationV2EventType.DATA_PRODUCT_VERSION_PUBLICATION_REQUESTED.getValue()
        ));
        return new PolicyV2AdapterObserver();
    }
}
