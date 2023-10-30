package org.opendatamesh.platform.pp.blueprint.server.resources.azure;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Embeddable;

@Embeddable
@Data
public class TeamProjectReferenceResource {

    @JsonProperty("name")
    private String projectName;

}
