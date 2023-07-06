package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProvisionInfoDPDS {

    @JsonProperty("service")
    private ReferenceObjectDPDS service;

    @JsonProperty("template")
    private ReferenceObjectDPDS template;
   
    @JsonProperty("configurations")
    private Map<String, Object> configurations;

    public ProvisionInfoDPDS() {
    }

    public ProvisionInfoDPDS(ReferenceObjectDPDS service, ReferenceObjectDPDS template, Map<String, Object> configurations) {
        this.service = service;
        this.template = template;
        this.configurations = configurations;
    }
}
