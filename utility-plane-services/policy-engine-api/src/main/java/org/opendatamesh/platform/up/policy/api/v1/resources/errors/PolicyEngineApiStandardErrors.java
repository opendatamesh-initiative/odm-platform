package org.opendatamesh.platform.up.policy.api.v1.resources.errors;

import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;

public enum PolicyEngineApiStandardErrors implements ODMApiStandardErrors {

    SC400_01_POLICY_EVAL_ID_IS_EMPTY ("40001", "Policy Evaluation ID cannot be null or empty"),
    SC400_02_POLICY_ID_IS_EMPTY ("40002", "Policy ID cannot be null or empty"),
    SC400_03_POLICY_RAW_CONTENT_IS_EMPTY ("40003", "Policy raw content cannot be null or empty"),
    SC400_04_OBJECT_TO_EVALUATE_IS_EMPTY ("40004", "Object to evaluate cannot be null or empty"),
    SC500_01_GENERIC_POLICY_ENGINE_ERROR("50001", "Unknown error from Policy Engine");

    private final String code;
    private final String description;

    PolicyEngineApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }

    public static PolicyEngineApiStandardErrors getByCode(String code) {
        for (PolicyEngineApiStandardErrors error : values()) {
            if (error.code.equals(code)) {
                return error;
            }
        }
        return SC500_01_GENERIC_POLICY_ENGINE_ERROR;
    }

}