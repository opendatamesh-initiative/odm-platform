package org.opendatamesh.platform.core.commons.git.resources.azure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class TeamProjectReferenceResource {

    @JsonProperty("id")
    private String projectId;

}
