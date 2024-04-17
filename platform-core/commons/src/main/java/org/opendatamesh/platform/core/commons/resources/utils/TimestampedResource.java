package org.opendatamesh.platform.core.commons.resources.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class TimestampedResource implements Serializable {

    @JsonProperty("createdAt")
    @Schema(description = "The creation timestamp. Automatically handled by the API: can not be modified.")
    private Date createdAt;

    @JsonProperty("updatedAt")
    @Schema(description = "The last update timestamp. Automatically handled by the API: can not be modified.")
    private Date updatedAt;

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

}