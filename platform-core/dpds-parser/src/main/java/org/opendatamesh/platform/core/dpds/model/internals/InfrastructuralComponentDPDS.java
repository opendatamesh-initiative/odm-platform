package org.opendatamesh.platform.core.dpds.model.internals;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.opendatamesh.platform.core.dpds.model.core.ComponentDPDS;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class InfrastructuralComponentDPDS extends ComponentDPDS {

    @JsonProperty("platform")
    @Schema(description = "Infrastructural Component platform", required = true)
    private String platform;
    
    @JsonProperty("infrastructureType")
    @Schema(description = "Infrastructural Component infrastructure type", required = true)
    private String infrastructureType;

    @JsonProperty("dependsOn")
    @Schema(description = "List of dependencies of the Infrastructural Component")
    private List<String> dependsOn = new ArrayList<String>();  
}
