package org.opendatamesh.platform.core.dpds.model.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.opendatamesh.platform.core.dpds.model.core.SpecificationExtensionPointDPDS;
import org.opendatamesh.platform.core.dpds.model.core.StandardDefinitionDPDS;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromisesDPDS {

    @JsonProperty("platform")
    @Schema(description = "Promises platform", required = true)
    protected String platform;

    @JsonProperty("servicesType")
    @Schema(description = "Type of the services of the Promises", required = true)
    protected String servicesType;

    @JsonProperty("api")
    @Schema(description = "Standard Definition object of the API of the Promises", required = true)
    protected StandardDefinitionDPDS api;


    @JsonProperty("deprecationPolicy")
    @Schema(description = "Specification Extension Point object of the deprecation policy of the Promises", required = true)
    protected SpecificationExtensionPointDPDS deprecationPolicy;

    @JsonProperty("slo")
    @Schema(description = "Specification Extension Point object of the slo of the Promises", required = true)
    protected SpecificationExtensionPointDPDS slo;
}
