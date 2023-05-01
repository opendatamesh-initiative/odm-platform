package org.opendatamesh.platform.pp.api.resources.v1.dataproduct;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class ApplicationComponentResource extends ComponentResource implements Cloneable{

    @JsonProperty("platform")
    private String platform;

    @JsonProperty("applicationType")
    private String applicationType;

    @JsonProperty("buildInfo")
    private BuildInfoResource buildInfo;

    @JsonProperty("deployInfo")
    private DeployInfoResource deployInfo;

    @JsonProperty("consumesFrom")
    private List<String> consumesFrom = new ArrayList<String>();
    @JsonProperty("providesTo")
    private List<String> providesTo = new ArrayList<String>();
    
    @JsonProperty("dependsOn")
    private List<String> dependsOn = new ArrayList<String>();

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();
    
    @JsonProperty("externalDocs")
    private ExternalResourceResource externalDocs;

    public ApplicationComponentResource() {
        
    }
    public ApplicationComponentResource(BuildInfoResource buildInfo, DeployInfoResource deploymentInfo) {
        this.buildInfo = buildInfo;
        this.deployInfo = deploymentInfo;
    }
}
