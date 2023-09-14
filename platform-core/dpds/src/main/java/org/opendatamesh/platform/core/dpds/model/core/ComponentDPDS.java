package org.opendatamesh.platform.core.dpds.model.core;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
//@JsonIgnoreProperties(value={ "id", "entityType" }, allowGetters=true, ignoreUnknown = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ComponentDPDS extends ReferenceableEntityDPDS {
       
    @JsonProperty("componentGroup")
    protected String componentGroup;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    protected String rawContent;

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();

    @JsonProperty("externalDocs")
    private ExternalResourceDPDS externalDocs;

    



}
