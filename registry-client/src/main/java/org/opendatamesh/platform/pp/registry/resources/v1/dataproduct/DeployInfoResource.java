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
    private ExternalResourceResource service;

    @JsonProperty("template")
    private ExternalResourceResource template;

    @JsonProperty("configurations")
    private Map<String, Object> configurations;

    public DeployInfoResource() {
        service= new ExternalResourceResource();
        template = new ExternalResourceResource();
        configurations = new HashMap<>();
    }

    public DeployInfoResource(ExternalResourceResource service, ExternalResourceResource template, Map<String, Object> configurations) {
        this.service = service;
        this.template = template;
        this.configurations = configurations;
    }

    public ExternalResourceResource getService() {
        return service;
    }

    public void setService(ExternalResourceResource service) {
        this.service = service;
    }
}
