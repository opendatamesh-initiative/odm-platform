package org.opendatamesh.platform.up.metaservice.server.services;

import org.opendatamesh.platform.up.notification.api.resources.NotificationResource;
import org.springframework.stereotype.Service;

@Service
public interface MetaService {

    NotificationResource handleDataProductCreated(NotificationResource notificationRes) throws MetaServiceException;

    NotificationResource handleDataProductUpdate(NotificationResource notificationResource) throws MetaServiceException;

    NotificationResource handleDataProductDelete(NotificationResource notificationRes) throws MetaServiceException;

}
