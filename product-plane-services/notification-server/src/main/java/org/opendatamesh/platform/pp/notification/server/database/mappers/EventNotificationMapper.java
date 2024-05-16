package org.opendatamesh.platform.pp.notification.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.core.commons.database.mappers.BaseMapper;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.server.database.entities.EventNotification;

@Mapper(componentModel = "spring")
public interface EventNotificationMapper extends BaseMapper<EventNotificationResource, EventNotification> {

}
