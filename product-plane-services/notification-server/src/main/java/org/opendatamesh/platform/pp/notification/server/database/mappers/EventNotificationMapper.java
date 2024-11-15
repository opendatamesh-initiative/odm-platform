package org.opendatamesh.platform.pp.notification.server.database.mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.opendatamesh.platform.core.commons.database.mappers.BaseMapper;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.server.database.entities.EventNotification;

@Mapper(componentModel = "spring")
public interface EventNotificationMapper extends BaseMapper<EventNotificationResource, EventNotification> {
    EventMapper INSTANCE = Mappers.getMapper(EventMapper.class);
    ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // Mapping methods for JsonNode to String and vice versa
    default String map(JsonNode value) throws JsonProcessingException {
        return value != null ? OBJECT_MAPPER.writeValueAsString(value) : null;
    }

    default JsonNode map(String value) throws JsonProcessingException {
        return value != null ? OBJECT_MAPPER.readTree(value) : null;
    }
}
