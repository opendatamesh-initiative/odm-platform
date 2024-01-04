package org.opendatamesh.platform.core.commons.git;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum GitStandardErrors implements ODMApiStandardErrors {

    // Unathorized Exceptions
    SC401_01_GIT_ERROR("40101", "User unauthorized"),

    // Forbidden Exceptions
    SC403_01_GIT_ERROR("40301", "Forbidden"),

    // Conflict
    SC409_01_GIT_CONFLICT("40901", "Conflicts in Git operations"),

    // Internal Server Error
    SC500_01_GIT_ERROR ("50001", "Error executing Git commands"),

    SC500_02_GIT_CLIENT_ERROR("50002", "Error creating Git client");

    private final String code;
    private final String description;

    GitStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() { return code; }

    public String description() { return description; }
}
