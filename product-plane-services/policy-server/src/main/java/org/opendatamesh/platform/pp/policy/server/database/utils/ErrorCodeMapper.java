package org.opendatamesh.platform.pp.policy.server.database.utils;

import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public final class ErrorCodeMapper {

    private static final Map<String, PolicyApiStandardErrors> ERROR_CODE_MAP = new HashMap<>();
    private static final Map<String, String> ERROR_MESSAGE_MAP = new HashMap<>();

    static {
        ERROR_CODE_MAP.put(PolicyEngine.class.getName(), PolicyApiStandardErrors.SC404_01_POLICY_ENGINE_NOT_FOUND);
        ERROR_CODE_MAP.put(Policy.class.getName(), PolicyApiStandardErrors.SC404_02_POLICY_NOT_FOUND);
        ERROR_CODE_MAP.put(PolicyEvaluationResult.class.getName(), PolicyApiStandardErrors.SC404_03_POLICY_EVALUATION_RESULT_NOT_FOUND);
    }

    static {
        ERROR_MESSAGE_MAP.put(PolicyEngine.class.getName(), "PolicyEngine with ID [%s] not found");
        ERROR_MESSAGE_MAP.put(Policy.class.getName(), "Policy with ID [%s] not found");
        ERROR_MESSAGE_MAP.put(PolicyEvaluationResult.class.getName(), "PolicyEvaluationResult with ID [%s] not found");
    }

    public static PolicyApiStandardErrors getErrorCodeForClass(String className) {
        return ERROR_CODE_MAP.get(className);
    }

    public static <ID extends Serializable> String getErrorMessageForClass(String className, ID identifier) {
        return String.format(ERROR_MESSAGE_MAP.get(className), identifier.toString());
    }
}
