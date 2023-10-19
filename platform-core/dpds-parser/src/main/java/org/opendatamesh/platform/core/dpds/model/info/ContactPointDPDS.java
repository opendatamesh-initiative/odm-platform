package org.opendatamesh.platform.core.dpds.model.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ContactPointDPDS {

    @JsonProperty("name")
    @Schema(description = "Contact Point name", required = true)
    private String name;

    @JsonProperty("description")
    @Schema(description = "Contact Point description", required = true)
    private String description;

    @JsonProperty("channel")
    @Schema(description = "Channel to contact the Contact Point", required = true)
    private String channel;

    @JsonProperty("address")
    @Schema(description = "Address to contact the Contact Point", required = true)
    private String address;
}
