package org.opendatamesh.platform.pp.blueprint.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.persistence.ElementCollection;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

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
    @Schema(description = "Base URL of the repository of the blueprint")
    private String repositoryUrl;

    @JsonProperty("blueprintPath")
    @Schema(description = "Relative path of the blueprint inside the repository described by 'repositoryUrl'")
    private String blueprintPath;

    @JsonProperty("targetPath")
    @Schema(description = "Relative path inside the repository described by 'repositoryUrl' of the target directory for the objects created from the blueprint")
    private String targetPath;

    /*@ElementCollection
    @JsonProperty("configurations")
    @Schema(description = "Set of <key-value> pairs representing parameters of the blueprint and their values")
    private Map<String, String> configurations;*/

    @JsonProperty("createdAt")
    @Schema(description = "Timestamp of the Blueprint creation")
    private Date createdAt;

    @JsonProperty("updatedAt")
    @Schema(description = "Timestamp of the Blueprint update")
    private Date updatedAt;

}
