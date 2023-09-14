package org.opendatamesh.platform.core.dpds.model.internals;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

import org.opendatamesh.platform.core.dpds.model.core.ComponentDPDS;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class InfrastructuralComponentDPDS extends ComponentDPDS {

    @JsonProperty("platform")
    private String platform;
    
    @JsonProperty("infrastructureType")
    private String infrastructureType;

    @JsonProperty("dependsOn")
    private List<String> dependsOn = new ArrayList<String>();  
}