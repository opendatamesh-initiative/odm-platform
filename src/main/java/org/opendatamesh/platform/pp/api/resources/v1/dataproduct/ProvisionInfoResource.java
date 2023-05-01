package org.opendatamesh.platform.pp.api.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProvisionInfoResource {

    @JsonProperty("service")
    private ExternalResourceResource service;

    @JsonProperty("template")
    private ExternalResourceResource template;
   
    @JsonProperty("configurations")
    private Map<String, Object> configurations;

    public ProvisionInfoResource() {
    }

    public ProvisionInfoResource(ExternalResourceResource service, ExternalResourceResource template, Map<String, Object> configurations) {
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

    public ExternalResourceResource getTemplate() {
        return template;
    }

    public void setTemplate(ExternalResourceResource template) {
        this.template = template;
    }

    public Map<String, Object> getConfigurations() {
        return configurations;
    }

    public void setConfigurations(Map<String, Object> configurations) {
        this.configurations = configurations;
    }
}
