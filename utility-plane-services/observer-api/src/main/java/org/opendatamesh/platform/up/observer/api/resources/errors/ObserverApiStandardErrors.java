package org.opendatamesh.platform.up.observer.api.resources.errors;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum ObserverApiStandardErrors implements ODMApiStandardErrors {

    SC500_01_GENERIC_OBSERVER_ERROR("50001", "Internal Server Error from Observer");

    private final String code;
    private final String description;

    ObserverApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }

}