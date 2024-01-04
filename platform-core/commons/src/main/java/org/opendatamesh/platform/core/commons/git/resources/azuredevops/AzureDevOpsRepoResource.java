package org.opendatamesh.platform.core.commons.git.resources.azuredevops;

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
