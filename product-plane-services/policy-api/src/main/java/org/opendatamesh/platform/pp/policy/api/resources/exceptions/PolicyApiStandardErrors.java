package org.opendatamesh.platform.pp.policy.api.resources.exceptions;

import com.google.common.collect.ImmutableMap;
import org.opendatamesh.platform.core.commons.servers.exceptions.ODMApiStandardErrors;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEngineResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyEvaluationResultResource;
import org.opendatamesh.platform.pp.policy.api.resources.PolicyResource;

import java.util.Map;

public enum PolicyApiStandardErrors implements ODMApiStandardErrors {

    SC400_01_POLICY_ENGINE_IS_EMPTY("40001", "PolicyEngine is empty"),
    SC400_02_POLICY_IS_EMPTY("40002", "PolicyEngine object cannot be empty"),
    SC400_03_POLICY_EVALUATION_RESULT_IS_EMPTY("40003", "PolicyEngine object cannot be empty"),

    SC404_01_POLICY_ENGINE_NOT_FOUND("40401", "Resource not found"),
    SC404_02_POLICY_NOT_FOUND("40402", "Resource not found"),
    SC404_03_POLICY_EVALUATION_RESULT_NOT_FOUND("40403", "Resource not found"),

    SC422_01_POLICY_ENGINE_IS_INVALID("42201", "PolicyEngine is invalid"),
    SC422_02_POLICY_IS_INVALID("42202", "Policy is invalid"),
    SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID("42203", "PolicyEvaluationResult is invalid"),

    SC422_04_POLICY_ALREADY_EXISTS("42204", "Policy already exists"),
    SC422_05_POLICY_ENGINE_ALREADY_EXISTS("42205", "PolicyEngine already exists");

    private final String code;
    private final String description;

    private static final Map<String, PolicyApiStandardErrors> IS_EMPTY_ERRORS = ImmutableMap
            .<String, PolicyApiStandardErrors>builder()
            .put(PolicyEngineResource.class.getName(), SC400_01_POLICY_ENGINE_IS_EMPTY)
            .put(PolicyResource.class.getName(), SC400_02_POLICY_IS_EMPTY)
            .put(PolicyEvaluationResultResource.class.getName(), SC400_03_POLICY_EVALUATION_RESULT_IS_EMPTY)
            .build();

    private static final Map<String, PolicyApiStandardErrors> NOT_FOUND_ERRORS = ImmutableMap
            .<String, PolicyApiStandardErrors>builder()
            .put(PolicyEngineResource.class.getName(), SC404_01_POLICY_ENGINE_NOT_FOUND)
            .put(PolicyResource.class.getName(), SC404_02_POLICY_NOT_FOUND)
            .put(PolicyEvaluationResultResource.class.getName(), SC404_03_POLICY_EVALUATION_RESULT_NOT_FOUND)
            .build();

    private static final Map<String, PolicyApiStandardErrors> IS_INVALID = ImmutableMap
            .<String, PolicyApiStandardErrors>builder()
            .put(PolicyEngineResource.class.getName(), SC422_01_POLICY_ENGINE_IS_INVALID)
            .put(PolicyResource.class.getName(), SC422_02_POLICY_IS_INVALID)
            .put(PolicyEvaluationResultResource.class.getName(), SC422_03_POLICY_EVALUATION_RESULT_IS_INVALID)
            .build();

    private static final Map<String, PolicyApiStandardErrors> ALREADY_EXISTS = ImmutableMap
            .<String, PolicyApiStandardErrors>builder()
            .put(PolicyEngineResource.class.getName(), SC422_05_POLICY_ENGINE_ALREADY_EXISTS)
            .put(PolicyResource.class.getName(), SC422_04_POLICY_ALREADY_EXISTS)
            .build();

    PolicyApiStandardErrors(String code, String description) {
        this.code = code;
        this.description = description;
    }
    public String code() { return code; }
    public String description() { return description; }

    public static  PolicyApiStandardErrors getIsEmptyError(String className){
        return IS_EMPTY_ERRORS.getOrDefault(className, null); //TODO define default error
    }
    public static PolicyApiStandardErrors getNotFoundError(String className){
        return NOT_FOUND_ERRORS.getOrDefault(className, null); //TODO define default error
    }
    public static PolicyApiStandardErrors getIsInvalidError(String className){
        return IS_INVALID.getOrDefault(className, null); //TODO define default error
    }
    public static PolicyApiStandardErrors getAlreadyExistsError(String className){
        return ALREADY_EXISTS.getOrDefault(className, null); //TODO define default error
    }
}
