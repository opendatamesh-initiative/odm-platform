package org.opendatamesh.platform.pp.registry.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProductDescriptorLocationResource {
    @JsonProperty("rootDocumentUri")
    @Schema(description = "Root path/URI of the Data Product Descriptor Location", required = true)
    private String rootDocumentUri;    

    @JsonProperty("git")
    @Schema(description = "Git object for the Data Product Descriptor Location", required = true)
    private Git git;    

    @Data
    public static class Git {
        @JsonProperty("repositorySshUri")
        @Schema(description = "SSH URI of the Git repository", required = true)
        private String repositorySshUri;

        @JsonProperty("branch")
        @Schema(description = "Branch of the Git repository", required = true)
        private String branch;

        @JsonProperty("tag")
        @Schema(description = "Tag of the Git repository")
        private String tag;
    }
}
