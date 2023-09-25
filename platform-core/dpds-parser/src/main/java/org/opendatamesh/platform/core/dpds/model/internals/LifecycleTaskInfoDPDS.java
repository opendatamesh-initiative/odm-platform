package org.opendatamesh.platform.core.dpds.model.internals;

import java.util.Map;

import org.opendatamesh.platform.core.dpds.model.core.ExternalResourceDPDS;
import org.opendatamesh.platform.core.dpds.model.core.StandardDefinitionDPDS;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LifecycleTaskInfoDPDS {

    @JsonProperty("name")
    private String name;

    @JsonIgnore
    private String stageName;

    @JsonProperty("order")
    private Integer order;

    @JsonProperty("service")
    private ExternalResourceDPDS service;

    @JsonProperty("template")
    private StandardDefinitionDPDS template;

    @JsonProperty("configurations")
    private Map<String, Object> configurations; 

    @JsonIgnore
    String rawContent;

    public boolean hasTemplate() {
        return template != null;
    }

    public boolean hasConfigurations() {
        return configurations != null;
    }
}
