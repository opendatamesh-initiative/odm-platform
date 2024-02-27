package org.opendatamesh.platform.pp.policy.api.resources.utils;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public abstract class TimestampedResource implements Serializable {

    @Schema(description = "The creation timestamp. Automatically handled by the API: can not be modified.")
    private Date createdAt;

    @Schema(description = "The last update timestamp. Automatically handled by the API: can not be modified.")
    private Date updatedAt;
}