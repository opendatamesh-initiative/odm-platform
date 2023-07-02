package org.opendatamesh.platform.pp.registry.api.v1.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProductDescriptorLocationResource {
    @JsonProperty("rootDocumentUri")
    private String rootDocumentUri;    

    @JsonProperty("git")
    private Git git;    

    @Data
    public static class Git {
        @JsonProperty("repositorySshUri")
        private String repositorySshUri;

        @JsonProperty("branch")
        private String branch;

        @JsonProperty("tag")
        private String tag;
    }
}
