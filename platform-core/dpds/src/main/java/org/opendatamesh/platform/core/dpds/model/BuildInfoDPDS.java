package org.opendatamesh.platform.core.dpds.model;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildInfoDPDS {

    @JsonProperty("service")
    private ExternalResourceDPDS service;

    @JsonProperty("template")
    private StandardDefinitionDPDS template;

   @JsonProperty("configurations")
    private Map<String, Object> configurations;
}
