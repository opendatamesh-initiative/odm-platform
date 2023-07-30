package org.opendatamesh.platform.core.dpds.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LifecycleActivityInfoDPDS {

    @JsonIgnore
    private String stageName;

    @JsonProperty("service")
    private ExternalResourceDPDS service;

    @JsonProperty("template")
    private StandardDefinitionDPDS template;

    @JsonProperty("configurations")
    private Map<String, Object> configurations; 

    @JsonIgnore
    String rawContent;
}
