package org.opendatamesh.platform.pp.event.notifier.api.resources.exceptions;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum EventNotifierApiStandardErrors implements ODMApiStandardErrors {

    SC400_01_LISTENER_IS_EMPTY("40001", "Listener is empty"),
    SC404_01_LISTENER_NOT_FOUND("40401", "Listener not found"),
    SC422_01_LISTENER_IS_INVALID("42201", "Listener is invalid"),
    SC422_02_LISTENER_ALREADY_EXISTS("42205", "Listener already exists");

    private final String code;
    private final String description;

    EventNotifierApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() { return code; }

    public String description() { return description; }
    
}
