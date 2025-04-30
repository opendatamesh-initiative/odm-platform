package org.opendatamesh.platform.pp.policy.server.client.notificationservice;

import org.opendatamesh.platform.pp.policy.server.client.utils.RestUtilsFactory;
import org.opendatamesh.platform.pp.policy.server.resources.notificationservice.NotificationEventResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class NotificationServiceClientConfigs {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Value("${odm.productPlane.notificationService.address:null}")
    private String address;

    @Value("${odm.productPlane.notificationService.active}")
    private boolean active;

    @Autowired
    private RestTemplate restTemplate;

    @Bean
    public NotificationServiceClient notificationServiceClient() {
        if (active) {
            return new NotificationServiceClientImpl(address, RestUtilsFactory.getRestUtils(restTemplate));
        } else {
            log.warn("Event Notification Client is not enabled in the configuration.");
            return new NotificationServiceClient() {
                @Override
                public void notifyEvent(NotificationEventResource eventResource) {
                    log.info("NotifyEvent called but notification client is disabled.");
                }
            };
        }
    }
}
