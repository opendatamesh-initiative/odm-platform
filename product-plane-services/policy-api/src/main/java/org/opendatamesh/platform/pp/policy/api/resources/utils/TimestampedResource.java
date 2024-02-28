package org.opendatamesh.platform.pp.policy.api.resources.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class TimestampedResource implements Serializable {

    @JsonProperty("createdAt")
    @Schema(description = "The creation timestamp. Automatically handled by the API: can not be modified.")
    private Date createdAt;

    @JsonProperty("updatedAt")
    @Schema(description = "The last update timestamp. Automatically handled by the API: can not be modified.")
    private Date updatedAt;

}