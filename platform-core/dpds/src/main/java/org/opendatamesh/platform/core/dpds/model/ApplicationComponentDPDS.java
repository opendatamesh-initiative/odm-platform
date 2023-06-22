package org.opendatamesh.platform.core.dpds.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ApplicationComponentDPDS extends ComponentDPDS implements Cloneable {

    @JsonProperty("platform")
    private String platform;

    @JsonProperty("applicationType")
    private String applicationType;

    @JsonProperty("buildInfo")
    private BuildInfoDPDS buildInfo;

    @JsonProperty("deployInfo")
    private DeployInfoDPDS deployInfo;

    @JsonProperty("consumesFrom")
    private List<String> consumesFrom = new ArrayList<String>();
    @JsonProperty("providesTo")
    private List<String> providesTo = new ArrayList<String>();
    
    @JsonProperty("dependsOn")
    private List<String> dependsOn = new ArrayList<String>();

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();
    
    @JsonProperty("externalDocs")
    private ExternalResourceDPDS externalDocs;

    public ApplicationComponentDPDS() {
        
    }
    public ApplicationComponentDPDS(BuildInfoDPDS buildInfo, DeployInfoDPDS deploymentInfo) {
        this.buildInfo = buildInfo;
        this.deployInfo = deploymentInfo;
    }
}
