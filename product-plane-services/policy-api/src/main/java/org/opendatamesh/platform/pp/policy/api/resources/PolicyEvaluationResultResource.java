package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.pp.policy.api.resources.utils.TimestampedResource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyEvaluationResultResource extends TimestampedResource {

    @JsonProperty("id")
    @Schema(description = "Auto-generated ID of the PolicyEvaluationResult")
    private Long id;

    @JsonProperty("dataProductId")
    @Schema(description = "ID of the Data Product evaluated (if the evaluation subject was a Data Product)")
    private String dataProductId;

    @JsonProperty("dataProductVersion")
    @Schema(description = "Version number of the Data Product evaluated (if the evaluation subject was a Data Product)")
    private String dataProductVersion;

    @JsonProperty("inputObject")
    @Schema(description = "JSON representation of the evaluated object")
    private JsonNode inputObject;

    @JsonProperty("outputObject")
    @Schema(description = "JSON representation of the evaluation output object")
    private String outputObject;

    @JsonProperty("result")
    @Schema(description = "Whether the evaluation is successful or not")
    private Boolean result;

    @JsonProperty("policyId")
    @Schema(description = "ID of the policy used for the evaluation")
    private Long policyId;

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

    public String getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(String dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }

    public JsonNode getInputObject() {
        return inputObject;
    }

    public void setInputObject(JsonNode inputObject) {
        this.inputObject = inputObject;
    }

    public String getOutputObject() {
        return outputObject;
    }

    public void setOutputObject(String outputObject) {
        this.outputObject = outputObject;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public Long getPolicyId() {
        return policyId;
    }

    public void setPolicyId(Long policyId) {
        this.policyId = policyId;
    }

}
