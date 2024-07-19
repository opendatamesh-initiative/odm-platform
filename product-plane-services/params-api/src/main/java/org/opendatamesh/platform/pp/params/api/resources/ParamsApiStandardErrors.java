package org.opendatamesh.platform.pp.params.api.resources;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum ParamsApiStandardErrors implements ODMApiStandardErrors {

    // Bad Request Exceptions
    SC400_01_PARAM_IS_EMPTY ("40001", "Blueprint is empty"),

    // Not Found Exceptions
    SC404_01_PARAMETER_NOT_FOUND ("40401", "Parameter not found"),

    // Unprocessable Entity Exceptions
    SC422_01_PARAMETER_IS_INVALID ("42201", "Parameter is invalid"),
    SC422_02_PARAMETER_ALREADY_EXISTS ("42202", "Parameter already exists"),

    // Internal Server Error
    SC500_03_ENCRYPTION_ERROR("50003", "Error in in the encryption service");

    private final String code;
    private final String description;

    ParamsApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String code() { return code; }

    public String description() { return description; }

}
