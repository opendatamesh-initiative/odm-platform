package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ValidationResponseResource {

    @JsonProperty("result")
    private Boolean result;

    @JsonProperty("policyResults")
    private List<PolicyEvaluationResultResource> policyResults;

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public List<PolicyEvaluationResultResource> getPolicyResults() {
        return policyResults;
    }

    public void setPolicyResults(List<PolicyEvaluationResultResource> policyResults) {
        this.policyResults = policyResults;
    }

}
