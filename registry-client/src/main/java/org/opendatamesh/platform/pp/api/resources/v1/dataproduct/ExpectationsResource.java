package org.opendatamesh.platform.pp.api.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpectationsResource {

    @JsonProperty("audience")
    protected SpecificationExtensionPointResource audience;
    @JsonProperty("usage")
    protected SpecificationExtensionPointResource usage;

}
