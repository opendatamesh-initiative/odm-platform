package org.opendatamesh.platform.pp.notification.api.resources.exceptions;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum EventNotifierApiStandardErrors implements ODMApiStandardErrors {

    SC400_01_OBSERVER_IS_EMPTY("40001", "Observer is empty"),
    SC400_02_EVENT_IS_EMPTY("40002", "Event is empty"),
    SC400_03_NOTIFICATION_IS_EMTPY("4003", "Notification is empty"),
    SC404_01_OBSERVER_NOT_FOUND("40401", "Observer not found"),
    SC404_02_NOTIFICATION_NOT_FOUND("40402", "Notification not found"),
    SC422_01_OBSERVER_IS_INVALID("42201", "Observer is invalid"),
    SC422_02_OBSERVER_ALREADY_EXISTS("42202", "Observer already exists"),
    SC422_03_NOTIFICATION_IS_INVALID("42203", "Notification is invalid"),
    SC422_04_NOTIFICATION_ALREADY_EXISTS("42204", "Notification already exists");

    private final String code;
    private final String description;

    EventNotifierApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() { return code; }

    public String description() { return description; }
    
}
