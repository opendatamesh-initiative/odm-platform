package org.opendatamesh.platform.pp.event.notifier.api.resources.exceptions;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum EventNotifierApiStandardErrors implements ODMApiStandardErrors {

    SC400_01_OBSERVER_IS_EMPTY("40001", "Observer is empty"),
    SC404_01_OBSERVER_NOT_FOUND("40401", "Observer not found"),
    SC422_01_OBSERVER_IS_INVALID("42201", "Observer is invalid"),
    SC422_02_OBSERVER_ALREADY_EXISTS("42205", "Observer already exists");

    private final String code;
    private final String description;

    EventNotifierApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() { return code; }

    public String description() { return description; }
    
}
