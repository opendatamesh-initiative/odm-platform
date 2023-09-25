package org.opendatamesh.platform.core.dpds.model.interfaces;

import org.opendatamesh.platform.core.dpds.model.core.SpecificationExtensionPointDPDS;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpectationsDPDS {

    @JsonProperty("audience")
    protected SpecificationExtensionPointDPDS audience;
    @JsonProperty("usage")
    protected SpecificationExtensionPointDPDS usage;

}
