package org.opendatamesh.platform.pp.blueprint.api.resources;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum BlueprintApiStandardErrors implements ODMApiStandardErrors {

    // Bad Request Exceptions
    SC400_01_BLUEPRINT_IS_EMPTY ("40001", "Blueprint is empty"),
    SC400_02_CONFIG_IS_EMPTY ("40002", "Config object is empty"),
    SC400_03_CONFIG_IS_INVALID ("40003", "Config object is invalid"),

    // Not Found Exceptions
    SC404_01_BLUEPRINT_NOT_FOUND ("40401", "Blueprint not found"),

    // Unprocessable Entity Exceptions
    SC422_01_BLUEPRINT_IS_INVALID ("42201", "Blueprint is invalid"),
    SC422_02_BLUEPRINT_ALREADY_EXISTS ("42202", "Blueprint already exists");

    private final String code;
    private final String description;

    BlueprintApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    
    public String code() { return code; }

    public String description() { return description; }

}
