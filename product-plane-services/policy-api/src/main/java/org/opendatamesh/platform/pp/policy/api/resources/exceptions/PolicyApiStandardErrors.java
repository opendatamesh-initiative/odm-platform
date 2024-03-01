package org.opendatamesh.platform.pp.policy.api.resources.exceptions;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum PolicyApiStandardErrors implements ODMApiStandardErrors {

    SC400_01_POLICY_ENGINE_IS_EMPTY("40001", "PolicyEngine is empty"),
    SC400_02_POLICY_IS_EMPTY("40002", "PolicyEngine object cannot be empty"),
    SC400_03_POLICY_EVALUATION_RESULT_IS_EMPTY("40003", "PolicyEngine object cannot be empty"),
    SC404_01_RESOURCE_NOT_FOUND("40404", "Resource not found"),

    SC422_01_POLICY_ENGINE_IS_INVALID("42201", "PolicyEngine is invalid"),
    SC422_02_POLICY_IS_INVALID("42202", "Policy is invalid"),
    SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID("42203", "PolicyEvaluationResult is invalid"),
    SC422_04_POLICY_ALREADY_EXISTS("42204", "Policy already exists"),
    SC422_05_POLICY_ENGINE_ALREADY_EXISTS("42205", "PolicyEngine already exists");

    private final String code;
    private final String description;

    PolicyApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }
}
