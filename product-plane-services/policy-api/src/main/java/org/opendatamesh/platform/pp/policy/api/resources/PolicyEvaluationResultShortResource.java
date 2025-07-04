package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.core.commons.resources.utils.TimestampedResource;

public class PolicyEvaluationResultShortResource extends TimestampedResource {

    @JsonProperty("id")
    @Schema(description = "Auto-generated ID of the PolicyEvaluationResult")
    private Long id;

    @JsonProperty("dataProductId")
    @Schema(description = "ID of the Data Product evaluated (if the evaluation subject was a Data Product)")
    private String dataProductId;

    @JsonProperty("result")
    @Schema(description = "Whether the evaluation is successful or not")
    private Boolean result;

    @JsonProperty("policy")
    @Schema(description = "The policy used for the evaluation")
    private PolicyResource policy;

    public PolicyEvaluationResultShortResource() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataProductId() {
        return dataProductId;
    }

    public void setDataProductId(String dataProductId) {
        this.dataProductId = dataProductId;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public PolicyResource getPolicy() {
        return policy;
    }

    public void setPolicy(PolicyResource policy) {
        this.policy = policy;
    }
} 