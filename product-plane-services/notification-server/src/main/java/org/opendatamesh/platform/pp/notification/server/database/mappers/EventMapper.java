package org.opendatamesh.platform.pp.notification.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.core.commons.database.mappers.BaseMapper;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.server.database.entities.Event;

@Mapper(componentModel = "spring")
public interface EventMapper extends BaseMapper<EventResource, Event> {
}
