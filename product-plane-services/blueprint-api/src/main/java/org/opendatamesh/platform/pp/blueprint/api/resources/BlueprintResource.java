package org.opendatamesh.platform.pp.blueprint.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlueprintResource {

    @JsonProperty("id")
    @Schema(description = "Auto generated Blueprint ID")
    private Long id;

    @JsonProperty("name")
    @Schema(description = "Blueprint name")
    private String name;

    @JsonProperty("version")
    @Schema(description = "Blueprint version")
    private String version;

    @JsonProperty("displayName")
    @Schema(description = "Blueprint name to display")
    private String displayName;

    @JsonProperty("description")
    @Schema(description = "Blueprint description")
    private String description;

    @JsonProperty("repositoryProvider")
    @Schema(description = "Repository Provider (e.g., Azure, GitHub, GitLab, ...)")
    private RepositoryProviderEnum repositoryProvider;

    @JsonProperty("repositoryUrl")
    @Schema(description = "SSH URL of the repository of the blueprint")
    private String repositoryUrl;

    @JsonProperty("organization")
    @Schema(description = "User/Organization of the blueprint repo in the Git provider")
    private String organization;

    @JsonProperty("projectName")
    @Schema(description = "Name of the project in the Git provider [Optional, needed for AzureDevOps]")
    private String projectName;

    @JsonProperty("createdAt")
    @Schema(description = "Timestamp of the Blueprint creation")
    private Date createdAt;

    @JsonProperty("updatedAt")
    @Schema(description = "Timestamp of the Blueprint update")
    private Date updatedAt;

}
