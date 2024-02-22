package org.opendatamesh.platform.pp.policy.api.resources;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum PolicyApiStandardErrors implements ODMApiStandardErrors {

    // Bad Request Exceptions
    SC400_01_POLICY_IS_EMPTY ("40001", "Policy object is empty"),

    SC400_02_ENGINE_IS_EMPTY ("40002", "Policy object is empty"),

    // Not Found Exceptions
    SC404_01_POLICY_NOT_FOUND ("40401", "Policy not found"),
    SC404_02_ENGINE_NOT_FOUND ("40402", "Suite not found"),

    // Unprocessable Entity Exceptions
    SC422_01_POLICY_IS_INVALID ("42201", "Policy object is invalid"),
    SC422_02_POLICY_ALREADY_EXISTS ("42202", "Policy already exists"),
    SC422_03_ENGINE_IS_INVALID ("40003", "Engine object is invalid"),
    SC422_04_ENGINE_ALREADY_EXISTS ("42204", "Engine already exists");

    private final String code;
    private final String description;

    PolicyApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() { return code; }

    public String description() { return description; }

}
