package org.opendatamesh.platform.pp.api.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceObjectResource {

    @JsonProperty("description")
    private String description;

    @JsonProperty("$ref")
    private String ref;

    @JsonIgnore
    private String originalRef;
    
    @JsonIgnore
    protected String rawContent;

    public ReferenceObjectResource() { }

    public ReferenceObjectResource(String ref, String description) {
        this.ref = ref;
        this.description = description;
    }
}
