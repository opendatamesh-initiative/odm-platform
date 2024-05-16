package org.opendatamesh.platform.pp.notification.server.database.mappers;

import org.mapstruct.Mapper;
import org.opendatamesh.platform.core.commons.database.mappers.BaseMapper;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.server.database.entities.Observer;

@Mapper(componentModel = "spring")
public interface ObserverMapper extends BaseMapper<ObserverResource, Observer> {
}
