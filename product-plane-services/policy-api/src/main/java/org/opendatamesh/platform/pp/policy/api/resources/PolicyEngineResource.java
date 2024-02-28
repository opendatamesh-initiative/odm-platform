package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opendatamesh.platform.pp.policy.api.resources.utils.TimestampedResource;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyEngineResource extends TimestampedResource {

    @JsonProperty("id")
    @Schema(description = "")
    private Long id;

    @JsonProperty("name")
    @Schema(description = "")
    private String name;

    @JsonProperty("displayName")
    @Schema(description = "")
    private String displayName;

    @JsonProperty("adapterUrl")
    @Schema(description = "")
    private String adapterUrl;

}
