package org.opendatamesh.paltform.pp.event.notifier.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.paltform.pp.event.notifier.server.database.entities.Listener;
import org.opendatamesh.platform.core.commons.database.mappers.BaseMapper;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ListenerResource;

@Mapper(componentModel = "spring")
public interface ListenerMapper extends BaseMapper<ListenerResource, Listener> {
}
