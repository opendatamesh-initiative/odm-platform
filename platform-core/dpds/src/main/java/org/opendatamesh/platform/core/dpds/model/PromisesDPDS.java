package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromisesDPDS {

    @JsonProperty("platform")
    protected String platform;

    @JsonProperty("servicesType")
    protected String servicesType;

    @JsonProperty("api")
    protected StandardDefinitionDPDS api;

    @JsonProperty("deprecationPolicy")
    protected SpecificationExtensionPointDPDS deprecationPolicy;

    @JsonProperty("slo")
    protected SpecificationExtensionPointDPDS slo;
}
