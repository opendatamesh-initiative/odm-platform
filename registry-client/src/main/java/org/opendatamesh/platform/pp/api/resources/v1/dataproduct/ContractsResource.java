package org.opendatamesh.platform.pp.api.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractsResource {

    @JsonProperty("termsAndConditions")
    protected SpecificationExtensionPointResource termsAndConditions;

    @JsonProperty("billingPolicy")
    protected SpecificationExtensionPointResource billingPolicy;
   
    @JsonProperty("sla")
    protected SpecificationExtensionPointResource sla;
}
