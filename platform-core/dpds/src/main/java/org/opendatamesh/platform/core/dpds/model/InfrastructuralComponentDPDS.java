package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class InfrastructuralComponentDPDS extends ComponentDPDS implements Cloneable {

    @JsonProperty("platform")
    private String platform;
    
    @JsonProperty("infrastructureType")
    private String infrastructureType;

    @JsonProperty("dependsOn")
    private List<String> dependsOn = new ArrayList<String>();

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();

    @JsonProperty("externalDocs")
    private ExternalResourceDPDS externalDocs;
  
}
