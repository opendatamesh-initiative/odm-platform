package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InfrastructuralComponentDPDS extends ComponentDPDS implements Cloneable {

    @JsonProperty("platform")
    private String platform;
    
    @JsonProperty("infrastructureType")
    private String infrastructureType;

    @JsonProperty("provisionInfo")
    private ProvisionInfoDPDS provisionInfo;

    @JsonProperty("dependsOn")
    private List<String> dependsOn = new ArrayList<String>();

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();

    @JsonProperty("externalDocs")
    private ExternalResourceDPDS externalDocs;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getInfrastructureType() {
        return infrastructureType;
    }

    public void setInfrastructureType(String infrastructureType) {
        this.infrastructureType = infrastructureType;
    }

    public ProvisionInfoDPDS getProvisionInfo() {
        return provisionInfo;
    }

    public void setProvisionInfo(ProvisionInfoDPDS provisionInfo) {
        this.provisionInfo = provisionInfo;
    }

    public List<String> getDependsOn() {
        return dependsOn;
    }

    public void setDependsOn(List<String> dependsOn) {
        this.dependsOn = dependsOn;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public ExternalResourceDPDS getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(ExternalResourceDPDS externalDocs) {
        this.externalDocs = externalDocs;
    }
}
