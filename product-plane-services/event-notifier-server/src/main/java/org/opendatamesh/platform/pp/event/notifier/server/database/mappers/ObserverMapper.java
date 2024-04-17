package org.opendatamesh.platform.pp.event.notifier.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.pp.event.notifier.server.database.entities.Observer;
import org.opendatamesh.platform.core.commons.database.mappers.BaseMapper;
import org.opendatamesh.platform.pp.event.notifier.api.resources.ObserverResource;

@Mapper(componentModel = "spring")
public interface ObserverMapper extends BaseMapper<ObserverResource, Observer> {
}
