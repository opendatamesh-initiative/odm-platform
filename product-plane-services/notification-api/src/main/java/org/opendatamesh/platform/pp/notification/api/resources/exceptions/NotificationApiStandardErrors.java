package org.opendatamesh.platform.pp.notification.api.resources.exceptions;

import com.google.common.collect.ImmutableMap;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;
import org.opendatamesh.platform.pp.notification.api.resources.EventNotificationResource;
import org.opendatamesh.platform.pp.notification.api.resources.EventResource;
import org.opendatamesh.platform.pp.notification.api.resources.ObserverResource;

import java.util.Map;

public enum NotificationApiStandardErrors implements ODMApiStandardErrors {

    SC400_01_OBSERVER_IS_EMPTY("40001", "Observer is empty"),
    SC400_02_EVENT_IS_EMPTY("40002", "Event is empty"),
    SC400_03_NOTIFICATION_IS_EMTPY("40003", "Notification is empty"),
    SC404_01_OBSERVER_NOT_FOUND("40401", "Observer not found"),
    SC404_02_EVENT_NOT_FOUND("40402", "Event not found"),
    SC404_03_NOTIFICATION_NOT_FOUND("40403", "Notification not found"),
    SC422_01_OBSERVER_IS_INVALID("42201", "Observer is invalid"),
    SC422_02_OBSERVER_ALREADY_EXISTS("42202", "Observer already exists"),
    SC422_03_EVENT_IS_INVALID("42203", "Event is invalid"),
    SC422_04_NOTIFICATION_IS_INVALID("42204", "Notification is invalid");

    private final String code;
    private final String description;

    private static final Map<String, NotificationApiStandardErrors> NOT_FOUND_ERRORS = ImmutableMap
            .<String, NotificationApiStandardErrors>builder()
            .put(ObserverResource.class.getName(), SC404_01_OBSERVER_NOT_FOUND)
            .put(EventResource.class.getName(), SC404_02_EVENT_NOT_FOUND)
            .put(EventNotificationResource.class.getName(), SC404_03_NOTIFICATION_NOT_FOUND)
            .build();

    NotificationApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() { return code; }

    public String description() { return description; }

    public static NotificationApiStandardErrors getNotFoundError(String className){
        return NOT_FOUND_ERRORS.getOrDefault(className, null); //TODO define default error
    }
    
}
