package org.opendatamesh.platform.pp.api.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildInfoResource {

    @JsonProperty("service")
    private ExternalResourceResource service;

    @JsonProperty("template")
    private ExternalResourceResource template;

    @JsonProperty("configurations")
    private Map<String, Object> configurations;

    public BuildInfoResource() {
        
    }
    public BuildInfoResource(ExternalResourceResource service, ExternalResourceResource template, Map<String, Object> configurations) {
        this.service = service;
        this.template = template;
        this.configurations = configurations;
    }
}
