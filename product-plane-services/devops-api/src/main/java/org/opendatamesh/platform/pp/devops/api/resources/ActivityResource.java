package org.opendatamesh.platform.pp.devops.api.resources;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityResource {

    @JsonProperty("id")
    @Schema(description = "Auto generated Activity ID")
    Long id;

    @JsonProperty("dataProductId")
    @Schema(description = "ID of the Data Product subject of the Activity", required = true)
    private String dataProductId;

    @JsonProperty("dataProductVersion")
    @Schema(description = "Version of the Data Product subject of the Activity", required = true)
    private String dataProductVersion;

    @JsonProperty("stage")
    @Schema(description = "Goal stage of the Activity", required = true)
    String stage;

    @JsonProperty("status")
    @Schema(description = "Auto updated Activity status")
    ActivityStatus status;

    @JsonProperty("results")
    @Schema(description = "Auto updated Activity execution results")
    String results;

    @JsonProperty("errors")
    @Schema(description = "Auto updated Activity execution errors")
    String errors;

    @JsonProperty("createdAt")
    @Schema(description = "Activity creation timestamp")
    private Date createdAt;

    @JsonProperty("startedAt")
    @Schema(description = "Timestamp of the Activity execution start")
    private Date startedAt;

    @JsonProperty("finishedAt")
    @Schema(description = "Timestamp of the Activity execution end")
    private Date finishedAt;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ActivityResource other = (ActivityResource) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (dataProductId == null) {
            if (other.dataProductId != null)
                return false;
        } else if (!dataProductId.equals(other.dataProductId))
            return false;
        if (dataProductVersion == null) {
            if (other.dataProductVersion != null)
                return false;
        } else if (!dataProductVersion.equals(other.dataProductVersion))
            return false;
        if (stage == null) {
            if (other.stage != null)
                return false;
        } else if (!stage.equals(other.stage))
            return false;
        if (status != other.status)
            return false;
        if (results == null) {
            if (other.results != null)
                return false;
        } else if (!results.equals(other.results))
            return false;
        if (errors == null) {
            if (other.errors != null)
                return false;
        } else if (!errors.equals(other.errors))
            return false;
        if (createdAt == null) {
            if (other.createdAt != null)
                return false;
        } else if (!createdAt.equals(other.createdAt))
            return false;
        if (startedAt == null) {
            if (other.startedAt != null)
                return false;
        } else if (!startedAt.equals(other.startedAt))
            return false;
        if (finishedAt == null) {
            if (other.finishedAt != null)
                return false;
        } else if (!finishedAt.equals(other.finishedAt))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((dataProductId == null) ? 0 : dataProductId.hashCode());
        result = prime * result + ((dataProductVersion == null) ? 0 : dataProductVersion.hashCode());
        result = prime * result + ((stage == null) ? 0 : stage.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + ((results == null) ? 0 : results.hashCode());
        result = prime * result + ((errors == null) ? 0 : errors.hashCode());
        result = prime * result + ((createdAt == null) ? 0 : createdAt.hashCode());
        result = prime * result + ((startedAt == null) ? 0 : startedAt.hashCode());
        result = prime * result + ((finishedAt == null) ? 0 : finishedAt.hashCode());
        return result;
    }
}