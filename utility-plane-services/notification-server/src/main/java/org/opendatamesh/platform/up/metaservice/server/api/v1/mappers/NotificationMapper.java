package org.opendatamesh.platform.up.metaservice.server.api.v1.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.up.metaservice.server.database.entities.Notification;
import org.opendatamesh.platform.up.notification.api.v1.resources.NotificationResource;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    Notification toEntity(NotificationResource resource);

    NotificationResource toResource(Notification entity);

    List<Notification> toEntities(List<NotificationResource> resources);

    List<NotificationResource> toResources(List<Notification> entities);

}
