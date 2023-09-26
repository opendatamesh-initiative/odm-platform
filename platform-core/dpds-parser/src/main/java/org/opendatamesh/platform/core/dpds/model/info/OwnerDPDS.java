package org.opendatamesh.platform.core.dpds.model.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerDPDS {

    @JsonProperty("id")
    @Schema(description = "Auto generated ID of the Owner")
    private String id;

    @JsonProperty("name")
    @Schema(description = "Owner name", required = true)
    private String name;
}
