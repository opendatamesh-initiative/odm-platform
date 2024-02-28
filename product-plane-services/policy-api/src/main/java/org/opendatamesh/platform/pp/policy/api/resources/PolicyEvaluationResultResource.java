package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opendatamesh.platform.pp.policy.api.resources.utils.TimestampedResource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyEvaluationResultResource extends TimestampedResource {
    private Long id;
    private String dataProductVersion;
    private String dataProductId;
    private String inputObject;
    private String outputObject;
    private Boolean result;
    private Long policyId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDataProductVersion() {
        return dataProductVersion;
    }

    public void setDataProductVersion(String dataProductVersion) {
        this.dataProductVersion = dataProductVersion;
    }

    public String getDataProductId() {
        return dataProductId;
    }

    public void setDataProductId(String dataProductId) {
        this.dataProductId = dataProductId;
    }

    public String getInputObject() {
        return inputObject;
    }

    public void setInputObject(String inputObject) {
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
