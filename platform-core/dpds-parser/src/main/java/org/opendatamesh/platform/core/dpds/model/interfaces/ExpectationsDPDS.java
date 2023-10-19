package org.opendatamesh.platform.core.dpds.model.interfaces;

import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.core.dpds.model.core.SpecificationExtensionPointDPDS;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpectationsDPDS {

    @JsonProperty("audience")
    @Schema(description = "Specification Extension Point object of the audience of the Expectation", required = true)
    protected SpecificationExtensionPointDPDS audience;
    @JsonProperty("usage")
    @Schema(description = "Specification Extension Point object of the usage of the Expectation", required = true)
    protected SpecificationExtensionPointDPDS usage;

}
