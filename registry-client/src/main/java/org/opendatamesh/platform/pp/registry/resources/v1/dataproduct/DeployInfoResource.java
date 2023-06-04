package org.opendatamesh.platform.pp.registry.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeployInfoResource {

    @JsonProperty("service")
    private ReferenceObjectResource service;

    @JsonProperty("template")
    private ReferenceObjectResource template;

    @JsonProperty("configurations")
    private Map<String, Object> configurations;

    public DeployInfoResource() {}

    public DeployInfoResource(ReferenceObjectResource service, ReferenceObjectResource template, Map<String, Object> configurations) {
        this.service = service;
        this.template = template;
        this.configurations = configurations;
    }
}
