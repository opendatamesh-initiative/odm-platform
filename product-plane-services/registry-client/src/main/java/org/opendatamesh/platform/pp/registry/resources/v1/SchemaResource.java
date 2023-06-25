package org.opendatamesh.platform.pp.registry.resources.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchemaResource {

    @JsonProperty("id")
    Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String version;

    @JsonProperty("mediaType")
    private String mediaType; 

    @JsonProperty("content")
    private String content;  
}
