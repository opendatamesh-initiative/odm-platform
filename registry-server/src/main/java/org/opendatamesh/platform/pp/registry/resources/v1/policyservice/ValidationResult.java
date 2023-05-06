package org.opendatamesh.platform.pp.registry.resources.v1.policyservice;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ValidationResult {


    @JsonProperty("decision_id")
    private String decisionId;

    @JsonProperty("result")
    private Result result;

    public ValidationResult() {
    }

    public String getDecisionId() {
        return decisionId;
    }

    public void setDecisionId(String decisionId) {
        this.decisionId = decisionId;
    }

    public Result getResult() {
        return result;
    }

    public void setResult(Result result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "decisionId='" + decisionId + '\'' +
                ", result=" + result +
                '}';
    }

}
