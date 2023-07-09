package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;



@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeployInfoDPDS {

    @JsonProperty("service")
    private ExternalResourceDPDS service;

    @JsonProperty("template")
    private StandardDefinitionDPDS template;

    @JsonProperty("configurations")
    private String configurations;
}
