package org.opendatamesh.platform.core.dpds.model.interfaces;

import org.opendatamesh.platform.core.dpds.model.core.SpecificationExtensionPointDPDS;
import org.opendatamesh.platform.core.dpds.model.core.StandardDefinitionDPDS;

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
