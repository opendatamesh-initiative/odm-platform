package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.pp.policy.api.resources.utils.TimestampedResource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyResource extends TimestampedResource {

    @JsonProperty("id")
    @Schema(description = "Auto-generated ID of the Policy")
    private Long id;

    @JsonProperty("rootId")
    @Schema(description = "ID of the parent Policy (the same of the previous ID if the policy was never updated)")
    private Long rootId;

    @JsonProperty("name")
    @Schema(description = "Unique name of the Policy")
    private String name;

    @JsonProperty("displayName")
    @Schema(description = "Human readable display name of the Policy")
    private String displayName;

    @JsonProperty("description")
    @Schema(description = "Policy description")
    private String description;

    @JsonProperty("blockingFlag")
    @Schema(description = "Whether the Policy is blocking or not in regards to the suite")
    private Boolean blockingFlag;

    @JsonProperty("rawContent")
    @Schema(description = "Raw content of the Policy implementation if exists")
    private String rawContent;

    @JsonProperty("suite")
    @Schema(description = "The name of the suite which the policy belongs")
    private String suite;

    @JsonProperty("evaluationEvent")
    @Schema(description = "A tag, a phase, something that identify in which stages of the lifecycle the Policy must be evaluated")
    private String evaluationEvent;


    @JsonProperty("filteringExpression")
    @Schema(description = "A SpEL expression to be evaluated on the input object of a validation request to exclude or include the Policy in the set of policies to be evaluated")
    private String filteringExpression;

    @JsonProperty("isLastVersion")
    @Schema(description = "Whether or not the Policy is the last version")
    private Boolean isLastVersion;

    @JsonProperty("policyEngine")
    @Schema(description = "The PolicyEngine that will evaluate the Policy")
    private PolicyEngineResource policyEngine;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRootId() {
        return rootId;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getBlockingFlag() {
        return blockingFlag;
    }

    public void setBlockingFlag(Boolean blockingFlag) {
        this.blockingFlag = blockingFlag;
    }

    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(String rawContent) {
        this.rawContent = rawContent;
    }

    public String getSuite() {
        return suite;
    }

    public void setSuite(String suite) {
        this.suite = suite;
    }

    public String getFilteringExpression() {
        return filteringExpression;
    }

    public void setFilteringExpression(String filteringExpression) {
        this.filteringExpression = filteringExpression;
    }

    public Boolean getLastVersion() {
        return isLastVersion;
    }

    public void setLastVersion(Boolean lastVersion) {
        isLastVersion = lastVersion;
    }

    public PolicyEngineResource getPolicyEngine() {
        return policyEngine;
    }

    public void setPolicyEngine(PolicyEngineResource policyEngine) {
        this.policyEngine = policyEngine;
    }

    public String getEvaluationEvent() {
        return evaluationEvent;
    }

    public void setEvaluationEvent(String evaluationEvent) {
        this.evaluationEvent = evaluationEvent;
    }

}
