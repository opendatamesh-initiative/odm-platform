package org.opendatamesh.platform.pp.notification.server.database.mappers;

import com.google.common.collect.ImmutableMap;
import org.opendatamesh.platform.core.commons.servers.exceptions.InternalServerException;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;
import org.opendatamesh.platform.pp.notification.server.database.entities.Event;
import org.opendatamesh.platform.pp.notification.server.database.entities.EventNotification;
import org.opendatamesh.platform.pp.notification.server.database.entities.Observer;

import java.util.Map;
import java.util.Optional;

public class EntitiesToResources {

    private EntitiesToResources() { }

    private static final Map<String, String> entityToResourceMap = ImmutableMap
            .<String, String>builder()
            .put(Observer.class.getName(), ObserverResource.class.getName())
            .put(Event.class.getName(), EventResource.class.getName())
            .put(EventNotification.class.getName(), EventNotificationResource.class.getName())
            .build();

    public static <T> String getResourceClassName(Class<T> entityClass) {

        return Optional.ofNullable(entityToResourceMap.get(entityClass.getName()))
                .orElseThrow(() -> new InternalServerException(
                        "Entity " + entityClass.getName() + " not mapped to a corresponding resource."
                ));
    }

}
