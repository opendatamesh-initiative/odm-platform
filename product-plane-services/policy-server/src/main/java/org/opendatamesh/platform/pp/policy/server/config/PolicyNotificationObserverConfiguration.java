package org.opendatamesh.platform.pp.policy.server.config;

import org.opendatamesh.platform.pp.notification.api.clients.NotificationClientImpl;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

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

    @Bean
    public PolicyNotificationObserverRegistrar policyNotificationObserverRegistrar() {
        return new PolicyNotificationObserverRegistrar();
    }

    public class PolicyNotificationObserverRegistrar {

        @PostConstruct
        public void registerObserver() {
            if (notificationServiceActive && notificationServiceAddress != null && !notificationServiceAddress.isEmpty()) {
                NotificationClientImpl observerNotificationClient = new NotificationClientImpl(notificationServiceAddress);
                try {
                    if (observerNotificationClient != null) {
                        // Construct the full URL including context path
                        String fullObserverUrl = policyServiceAddress;
                        if (contextPath != null && !contextPath.isEmpty()) {
                            fullObserverUrl += contextPath.endsWith("/") ? contextPath.substring(0, contextPath.length() - 1) : contextPath;
                        }
                        
                        ObserverResource observer = new ObserverResource();
                        observer.setName(policyServiceName);
                        observer.setDisplayName("Policy Server");
                        observer.setObserverServerBaseUrl(fullObserverUrl);
                        
                        observerNotificationClient.addObserver(observer);
                        logger.info("Successfully registered Policy Server as observer with name: {} and URL: {}", 
                                   policyServiceName, fullObserverUrl);
                    }
                } catch (Exception e) {
                    logger.error("Failed to register Policy Server as observer", e);
                }
            } else {
                logger.warn("Skipping observer registration - notification service is not active");
            }
        }
    }
}
