package org.opendatamesh.platform.up.metaservice.server.services;

import org.opendatamesh.platform.up.notification.api.v1.resources.NotificationResource;
import org.springframework.stereotype.Service;

@Service
public interface MetaService {

    public NotificationResource handleDataProductCreated(NotificationResource notificationRes) throws MetaServiceException;

    public NotificationResource handleDataProductUpdate(NotificationResource notificationResource) throws MetaServiceException;

    public NotificationResource handleDataProductDelete(NotificationResource notificationRes) throws MetaServiceException;
    
}
