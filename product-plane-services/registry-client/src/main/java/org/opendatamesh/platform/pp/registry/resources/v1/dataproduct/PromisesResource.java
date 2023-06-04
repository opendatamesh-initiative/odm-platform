package org.opendatamesh.platform.pp.registry.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromisesResource {

    @JsonProperty("platform")
    protected String platform;

    @JsonProperty("servicesType")
    protected String servicesType;

    @JsonProperty("api")
    protected StandardDefinitionResource api;

    @JsonProperty("deprecationPolicy")
    protected SpecificationExtensionPointResource deprecationPolicy;

    @JsonProperty("slo")
    protected SpecificationExtensionPointResource slo;
}
