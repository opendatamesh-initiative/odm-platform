package org.opendatamesh.platform.pp.blueprint.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class BlueprintResource {

    @JsonProperty("id")
    @Schema(description = "Auto generated Blueprint ID")
    Long id;

    @JsonProperty("name")
    @Schema(description = "Blueprint name")
    String name;

    @JsonProperty("version")
    @Schema(description = "Blueprint version")
    String version;

    @JsonProperty("name")
    @Schema(description = "Blueprint name to display")
    String displayName;

    @JsonProperty("description")
    @Schema(description = "Blueprint description")
    String description;

    @JsonProperty("repositoryProvider")
    @Schema(description = "Repository Provider (e.g., Azure, GitHub, GitLab, ...)", allowableValues = {"AZURE_DEVOPS", "GITHUB"})
    RepositoryProviderEnum repositoryProvider;

    @JsonProperty("repositoryUrl")
    @Schema(description = "Base URL of the repository of the blueprint")
    String repositoryUrl;

    @JsonProperty("blueprintPath")
    @Schema(description = "Relative path of the blueprint inside the repository described by 'repositoryUrl'")
    String blueprintPath;

    @JsonProperty("targetPath")
    @Schema(description = "Relative path inside the repository described by 'repositoryUrl' of the target directory for the objects created from the blueprint")
    String targetPath;

    @JsonProperty("configurations")
    @Schema(description = "Set of <key-value> pairs representing parameters of the blueprint and their values")
    Map<String, String> configurations;

    @JsonProperty("startedAt")
    @Schema(description = "Timestamp of the Blueprint creation")
    private Date createdAt;

    @JsonProperty("finishedAt")
    @Schema(description = "Timestamp of the Blueprint update")
    private Date updatedAt;

}
