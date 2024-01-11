package org.opendatamesh.platform.core.dpds.model.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;


@Data
//@JsonIgnoreProperties(value={ "id", "entityType" }, allowGetters=true, ignoreUnknown = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ComponentDPDS extends ReferenceableEntityDPDS {
       
    @JsonProperty("componentGroup")
    @Schema(description = "Component group", required = true)
    protected String componentGroup;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @Schema(description = "Raw content of the Component", required = true)
    protected String rawContent;

    @JsonProperty("tags")
    @Schema(description = "List of tags of the Component", required = true)
    private List<String> tags = new ArrayList<String>();

    @JsonProperty("externalDocs")
    @Schema(description = "Document of the External Resource of the Component", required = true)
    private ExternalResourceDPDS externalDocs;

}
