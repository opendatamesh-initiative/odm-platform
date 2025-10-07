package org.opendatamesh.platform.pp.devops.server.configurations;

import org.opendatamesh.platform.pp.notification.api.clients.NotificationClientImpl;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class DevopsNotificationObserverConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(DevopsNotificationObserverConfiguration.class);

    @Value("${odm.productPlane.notificationService.active:false}")
    private boolean notificationServiceActive;

    @Value("${odm.productPlane.notificationService.address:}")
    private String notificationServiceAddress;

    @Value("${odm.productPlane.devopsService.address:}")
    private String devopsServiceAddress;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${odm.productPlane.devopsService.name:devops-server}")
    private String devopsServiceName;

    @Bean
    public DevopsNotificationObserverRegistrar devopsNotificationObserverRegistrar() {
        return new DevopsNotificationObserverRegistrar();
    }

    public class DevopsNotificationObserverRegistrar {

        @PostConstruct
        public void registerObserver() {
            if (notificationServiceActive && notificationServiceAddress != null && !notificationServiceAddress.isEmpty()) {
                NotificationClientImpl observerNotificationClient = new NotificationClientImpl(notificationServiceAddress);
                try {
                    // Construct the full URL including context path
                    String fullObserverUrl = devopsServiceAddress;
                    if (contextPath != null && !contextPath.isEmpty()) {
                        fullObserverUrl += contextPath.endsWith("/") ? contextPath.substring(0, contextPath.length() - 1) : contextPath;
                    }

                    ObserverResource observer = new ObserverResource();
                    observer.setName(devopsServiceName);
                    observer.setDisplayName("DevOps Server");
                    observer.setObserverServerBaseUrl(fullObserverUrl);

                    observerNotificationClient.addObserver(observer);
                    logger.info("Successfully registered DevOps Server as observer with name: {} and URL: {}",
                               devopsServiceName, fullObserverUrl);
                } catch (Exception e) {
                    logger.error("Failed to register DevOps Server as observer", e);
                }
            } else {
                logger.warn("Skipping observer registration - notification service is not active");
            }
        }
    }
}
