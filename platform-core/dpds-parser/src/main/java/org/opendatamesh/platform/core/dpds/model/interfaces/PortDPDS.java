package org.opendatamesh.platform.core.dpds.model.interfaces;

import io.swagger.v3.oas.annotations.media.Schema;
import org.opendatamesh.platform.core.dpds.model.core.ComponentDPDS;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;


@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
public class PortDPDS extends ComponentDPDS {

    @JsonProperty("promises")
    @Schema(description = "Promises object of the Port")
    protected PromisesDPDS promises;

    @JsonProperty("expectations")
    @Schema(description = "Expectations object of the Port")
    protected ExpectationsDPDS expectations;

    @JsonProperty("contracts")
    @Schema(description = "Contracts object of the Port")
    protected ContractsDPDS contracts;

    public boolean hasPromises() {
        return promises != null;
    }

    public boolean hasApi() {
        return hasPromises() && promises.getApi() != null;
    }

    public boolean hasApiDefinition() {
        return hasApi() && promises.getApi().getDefinition() != null;
    }

}

