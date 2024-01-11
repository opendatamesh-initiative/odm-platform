package org.opendatamesh.platform.core.dpds.model.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.opendatamesh.platform.core.dpds.model.core.SpecificationExtensionPointDPDS;

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
