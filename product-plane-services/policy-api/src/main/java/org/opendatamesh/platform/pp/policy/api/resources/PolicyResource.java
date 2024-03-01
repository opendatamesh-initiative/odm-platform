package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.opendatamesh.platform.pp.policy.api.resources.utils.TimestampedResource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyResource extends TimestampedResource {
    private Long id;
    private Long rootId;
    private String name;
    private String displayName;
    private String description;
    private Boolean blockingFlag;
    private String rawContent;
    private String suite;
    private Boolean isLastVersion;
    private Long policyEngineId;

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

    public Boolean getLastVersion() {
        return isLastVersion;
    }

    public void setLastVersion(Boolean lastVersion) {
        isLastVersion = lastVersion;
    }

    public Long getPolicyEngineId() {
        return policyEngineId;
    }

    public void setPolicyEngineId(Long policyEngineId) {
        this.policyEngineId = policyEngineId;
    }
}
