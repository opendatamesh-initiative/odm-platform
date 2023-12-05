package org.opendatamesh.platform.pp.blueprint.server.resources.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GitRepoResource {

    @JsonProperty("name")
    String name;

}
