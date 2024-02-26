package org.opendatamesh.platform.pp.policy.api.resources.exceptions;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum PolicyApiStandardErrors implements ODMApiStandardErrors {
    SC404_01_RESOURCE_NOT_FOUND("40401", "Resource not found"),
    SC422_01_MALFORMED_RESOURCE ("42201", "Resource has an incorrect identifier");

    private final String code;
    private final String description;

    PolicyApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }
}
