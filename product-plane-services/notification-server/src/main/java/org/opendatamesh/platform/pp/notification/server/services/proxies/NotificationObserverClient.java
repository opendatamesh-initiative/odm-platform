package org.opendatamesh.platform.pp.notification.server.services.proxies;

import com.fasterxml.jackson.databind.JsonNode;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.RestUtils;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.RestUtilsFactory;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.exceptions.ClientException;
import org.opendatamesh.platform.pp.notification.server.services.utils.rest.exceptions.ClientResourceMappingException;
import org.opendatamesh.platform.up.observer.api.clients.ObserverAPIRoutes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

@Service
public class NotificationObserverClient {

    private static final Logger logger = LoggerFactory.getLogger(NotificationObserverClient.class);
    @Autowired
    private RestTemplate restTemplate;

    public void dispatchEventNotificationToObserver(
            EventNotificationResource notificationToDispatch,
            ObserverResource observer
    ) {
        if (observer == null || notificationToDispatch == null || observer.getName() == null || notificationToDispatch.getEvent() == null || !StringUtils.hasText(notificationToDispatch.getEvent().getType())) {
            logger.warn("Malformed notification event or observer.");
            return;
        }

        RestUtils restUtils = RestUtilsFactory.getRestUtils(restTemplate);
        logger.info("Dispatching event: {} to observer: {}", notificationToDispatch.getEvent().getType(), observer.getName());

        try {
            restUtils.genericPost(
                    observer.getObserverServerBaseUrl() + ObserverAPIRoutes.CONSUME.getPath(),
                    null,
                    notificationToDispatch,
                    JsonNode.class
            );
            logger.info("Successfully sent notification to Observer: {}", observer.getName());
        } catch (ClientException e) {
            logger.warn("Observer: {} client exception: {}", observer.getName(), e.getMessage(), e);
        } catch (ClientResourceMappingException e) {
            logger.warn("Observer: {} resource mapping exception: {}", observer.getName(), e.getMessage(), e);
        }

    }

}
