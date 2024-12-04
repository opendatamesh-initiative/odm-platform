package org.opendatamesh.platform.pp.policy.api.resources;

import io.swagger.v3.oas.annotations.media.Schema;

public class PolicySearchOptions {

    @Schema(description = "Retrieve all the policies triggered by this evaluation event.")
    private String evaluationEvent;

    @Schema(description = "Retrieve all the policies associated to this policy engine.")
    private String policyEngineName;

    @Schema(description = "Retrieve all the policies with this name.")
    private String name;

    @Schema(description = "Retrieve only the last version of the policies.", defaultValue = "true")
    private Boolean lastVersion = true;

    public String getEvaluationEvent() {
        return evaluationEvent;
    }

    public String getPolicyEngineName() {
        return policyEngineName;
    }

    public void setPolicyEngineName(String policyEngineName) {
        this.policyEngineName = policyEngineName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(Boolean lastVersion) {
        this.lastVersion = lastVersion;
    }

    public void setEvaluationEvent(String evaluationEvent) {
        this.evaluationEvent = evaluationEvent;
    }

}
