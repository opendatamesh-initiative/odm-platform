package org.opendatamesh.platform.pp.devops.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LifecycleResource {

    @JsonProperty("id")
    @Schema(description = "Auto generated Lifecyle ID")
    private Long id;

    @JsonProperty("dataProductId")
    @Schema(description = "ID of the Data Product subject of the Lifecycle object")
    private String dataProductId;

    @JsonProperty("dataProductVersion")
    @Schema(description = "Version of the Data Product subject of the Lifecycle object")
    private String dataProductVersion;

    @JsonProperty("stage")
    @Schema(description = "Lifecycle stage")
    private String stage;

    @JsonProperty("results")
    @Schema(description = "Results of the execution of the tasks for the current stage")
    private String results;

    @JsonProperty("startedAt")
    @Schema(description = "Date from which the stage is valid for the Data Product")
    private Date startedAt;

    @JsonProperty("finishedAt")
    @Schema(description = "Date at which the stage is no longer valid for the Data Product")
    private Date finishedAt;

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LifecycleResource other = (LifecycleResource) obj;
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
        result = prime * result + ((startedAt == null) ? 0 : startedAt.hashCode());
        result = prime * result + ((finishedAt == null) ? 0 : finishedAt.hashCode());
        return result;
    }

}
