package org.opendatamesh.platform.pp.registry.resources.v1.dataproduct;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class InfrastructuralComponentResource extends ComponentResource implements Cloneable {

    @JsonProperty("platform")
    private String platform;
    
    @JsonProperty("infrastructureType")
    private String infrastructureType;

    @JsonProperty("provisionInfo")
    private ProvisionInfoResource provisionInfo;

    @JsonProperty("dependsOn")
    private List<String> dependsOn = new ArrayList<String>();

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();

    @JsonProperty("externalDocs")
    private ExternalResourceResource externalDocs;

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

    public ProvisionInfoResource getProvisionInfo() {
        return provisionInfo;
    }

    public void setProvisionInfo(ProvisionInfoResource provisionInfo) {
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

    public ExternalResourceResource getExternalDocs() {
        return externalDocs;
    }

    public void setExternalDocs(ExternalResourceResource externalDocs) {
        this.externalDocs = externalDocs;
    }
}
