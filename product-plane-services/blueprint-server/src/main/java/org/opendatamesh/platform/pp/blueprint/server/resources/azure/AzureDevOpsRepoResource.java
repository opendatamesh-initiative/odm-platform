package org.opendatamesh.platform.pp.blueprint.server.resources.azure;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Embedded;

@Data
public class AzureDevOpsRepoResource {

    @JsonProperty("name")
    String name;

    @JsonProperty("project")
    @Embedded
    TeamProjectReferenceResource teamProjectReferenceResource;

}
