package org.opendatamesh.platform.pp.policy.server.config;

import org.opendatamesh.platform.pp.notification.api.clients.NotificationClientImpl;
import org.opendatamesh.platform.pp.notification.api.clients.NotificationClientV2Impl;
import org.opendatamesh.platform.pp.notification.api.resources.v1.ObserverResource;
import org.opendatamesh.platform.pp.notification.api.resources.v2.EventV2SubscribeResponseResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.List;

@Configuration
public class PolicyNotificationObserverConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(PolicyNotificationObserverConfiguration.class);

    @Value("${odm.productPlane.notificationService.active:false}")
    private boolean notificationServiceActive;

    @Value("${odm.productPlane.notificationService.address:}")
    private String notificationServiceAddress;

    @Value("${odm.productPlane.policyService.address:}")
    private String policyServiceAddress;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${odm.productPlane.policyService.name:policy-server}")
    private String policyServiceName;

    @Value("${odm.productPlane.notificationService.apiVersion:v1}")
    private String apiVersion;

    @Bean
    public PolicyNotificationObserverRegistrar policyNotificationObserverRegistrar() {
        return new PolicyNotificationObserverRegistrar();
    }

    public class PolicyNotificationObserverRegistrar {

        @PostConstruct
        public void registerObserver() {
            if (!notificationServiceActive || notificationServiceAddress == null || notificationServiceAddress.isEmpty()) {
                logger.warn("Skipping observer registration - notification service is not active");
                return;
            }

            String fullObserverUrl = policyServiceAddress;
            if (contextPath != null && !contextPath.isEmpty()) {
                fullObserverUrl += contextPath.endsWith("/") ? contextPath.substring(0, contextPath.length() - 1) : contextPath;
            }

            switch (apiVersion.toUpperCase()) {
                case "V1": {
                    NotificationClientImpl observerNotificationClient = new NotificationClientImpl(notificationServiceAddress);
                    ObserverResource observer = new ObserverResource();
                    observer.setName(policyServiceName);
                    observer.setDisplayName("Policy Server");
                    observer.setObserverServerBaseUrl(fullObserverUrl);
                    observerNotificationClient.addObserver(observer);
                    logger.info("Successfully registered Policy Server as observer (V1) with name: {} and URL: {}",
                            policyServiceName, fullObserverUrl);
                    break;
                }
                case "V2": {
                    NotificationClientV2Impl clientV2 = new NotificationClientV2Impl(notificationServiceAddress);
                    EventV2SubscribeResponseResource.EventV2SubscribeResource subscribeResource =
                            new EventV2SubscribeResponseResource.EventV2SubscribeResource();
                    subscribeResource.setName(policyServiceName);
                    subscribeResource.setDisplayName("Policy Server");
                    subscribeResource.setObserverBaseUrl(fullObserverUrl);
                    subscribeResource.setObserverApiVersion("V2");
                    subscribeResource.setEventTypes(List.of("DATA_PRODUCT_DELETED", "DATA_PRODUCT_VERSION_DELETED"));
                    clientV2.subscribeObserverV2(subscribeResource);
                    logger.info("Successfully registered Policy Server as observer (V2) with name: {} and URL: {}",
                            policyServiceName, fullObserverUrl);
                    break;
                }
                default: {
                    logger.warn("Unsupported apiVersion: {}. No observer registered.", apiVersion);
                    break;
                }
            }
        }
    }
}
