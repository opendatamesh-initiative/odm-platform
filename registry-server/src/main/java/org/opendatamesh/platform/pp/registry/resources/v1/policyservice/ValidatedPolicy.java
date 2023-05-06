package org.opendatamesh.platform.pp.registry.resources.v1.policyservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidatedPolicy {

    @JsonProperty("validationResult")
    private ValidationResult validationResult;

    @JsonProperty("policy")
    @Enumerated(EnumType.STRING)
    private PolicyName policy;

    public ValidatedPolicy() {
    }

    @Override
    public String toString() {
        return "ValidatedPolicy{" +
                "validationResult=" + validationResult +
                ", policy='" + policy + '\'' +
                '}';
    }

    public ValidationResult getValidationResult() {
        return validationResult;
    }

    public void setValidationResult(ValidationResult validationResult) {
        this.validationResult = validationResult;
    }

    public PolicyName getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyName policy) {
        this.policy = policy;
    }
}
