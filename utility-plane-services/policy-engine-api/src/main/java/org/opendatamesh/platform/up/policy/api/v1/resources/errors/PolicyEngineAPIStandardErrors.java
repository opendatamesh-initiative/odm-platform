package org.opendatamesh.platform.up.policy.api.v1.resources.errors;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;

public enum PolicyEngineAPIStandardErrors implements ODMApiStandardErrors {

    SC400_01_POLICY_EVAL_ID_IS_EMPTY ("40001", "Policy Evaluation ID cannot be null or empty"),
    SC400_02_POLICY_ID_IS_EMPTY ("40002", "Policy ID cannot be null or empty"),
    SC400_03_POLICY_RAW_CONTENT_IS_EMPTY ("40003", "Policy raw content cannot be null or empty"),
    SC400_04_OBJECT_TO_EVALUATE_IS_EMPTY ("40004", "Object to evaluate cannot be null or empty");

    private final String code;
    private final String description;

    PolicyEngineAPIStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }

}