package org.opendatamesh.platform.core.dpds.model.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.opendatamesh.platform.core.dpds.model.core.SpecificationExtensionPointDPDS;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContractsDPDS {

    @JsonProperty("termsAndConditions")
    @Schema(description = "Contracts terms and conditions", required = true)
    protected SpecificationExtensionPointDPDS termsAndConditions;

    @JsonProperty("billingPolicy")
    @Schema(description = "Contracts billing policy", required = true)
    protected SpecificationExtensionPointDPDS billingPolicy;
   
    @JsonProperty("sla")
    @Schema(description = "Specification Extension Point of the Contracts", required = true)
    protected SpecificationExtensionPointDPDS sla;
}
