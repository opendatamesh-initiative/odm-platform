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
public class ApplicationComponentDPDS extends ComponentDPDS {


    @JsonProperty("platform")
    @Schema(description = "Application Component platform", required = true)
    private String platform;

    @JsonProperty("applicationType")
    @Schema(description = "Application Component type", required = true)
    private String applicationType;

    @JsonProperty("consumesFrom")
    @Schema(description = "List of origins from which the Application Component consumes", required = true)
    private List<String> consumesFrom = new ArrayList<String>();
    @JsonProperty("providesTo")
    @Schema(description = "List of destinations the Application Component provides", required = true)
    private List<String> providesTo = new ArrayList<String>();
    
    @JsonProperty("dependsOn")
    @Schema(description = "List of Application Component dependencies", required = true)
    private List<String> dependsOn = new ArrayList<String>();

}
