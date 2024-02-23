package org.opendatamesh.platform.pp.policy.api.resources.exceptions;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum PolicyApiStandardErrors implements ODMApiStandardErrors {
    SC400_01_MALFORMED_RESOURCE ("40404", "Resource has an incorrect identifier"),
    SC404_01_RESOURCE_NOT_FOUND("40401", "Resource not found");

    private final String code;
    private final String description;

    PolicyApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }
}
