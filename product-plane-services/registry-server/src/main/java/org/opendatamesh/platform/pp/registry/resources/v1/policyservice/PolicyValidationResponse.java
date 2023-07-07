package org.opendatamesh.platform.pp.registry.resources.v1.policyservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyValidationResponse {

    @JsonProperty("validatedPolicies")
    private List<ValidatedPolicy> validatedPolicyList;

    public PolicyValidationResponse() {
    }

    public List<ValidatedPolicy> getValidatedPolicyList() {
        return validatedPolicyList;
    }

    public void setValidatedPolicyList(List<ValidatedPolicy> validatedPolicyList) {
        this.validatedPolicyList = validatedPolicyList;
    }

    @Override
    public String toString() {
        return "PolicyValidationResponse{" +
                "validatedPolicyList=" + validatedPolicyList +
                '}';
    }
}
