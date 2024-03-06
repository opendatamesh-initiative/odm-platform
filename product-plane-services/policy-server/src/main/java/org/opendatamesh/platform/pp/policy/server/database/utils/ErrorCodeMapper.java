package org.opendatamesh.platform.pp.policy.server.database.utils;

import com.google.common.collect.ImmutableMap;
import org.opendatamesh.platform.pp.policy.api.resources.exceptions.PolicyApiStandardErrors;
import org.opendatamesh.platform.pp.policy.server.database.entities.Policy;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEngine;
import org.opendatamesh.platform.pp.policy.server.database.entities.PolicyEvaluationResult;

import java.io.Serializable;
import java.util.Map;

public final class ErrorCodeMapper {

    private static final Map<Class, PolicyApiStandardErrors> ERROR_CODE_MAP = ImmutableMap
            .<Class, PolicyApiStandardErrors>builder()
            .put(PolicyEngine.class, PolicyApiStandardErrors.SC404_01_POLICY_ENGINE_NOT_FOUND)
            .put(Policy.class, PolicyApiStandardErrors.SC404_02_POLICY_NOT_FOUND)
            .put(PolicyEvaluationResult.class, PolicyApiStandardErrors.SC404_03_POLICY_EVALUATION_RESULT_NOT_FOUND)
            .build();

    private static final Map<Class, String> ERROR_MESSAGE_MAP = ImmutableMap
            .<Class, String>builder()
            .put(PolicyEngine.class, "PolicyEngine with ID [%s] not found")
            .put(Policy.class, "Policy with ID [%s] not found")
            .put(PolicyEvaluationResult.class, "PolicyEvaluationResult with ID [%s] not found")
            .build();

    public static PolicyApiStandardErrors getErrorCodeForClass(Class className) {
        return ERROR_CODE_MAP.get(className);
    }

    public static <ID extends Serializable> String getErrorMessageForClass(Class className, ID identifier) {
        return String.format(ERROR_MESSAGE_MAP.get(className), identifier.toString());
    }

}
