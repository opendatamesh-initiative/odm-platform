package org.opendatamesh.platform.core.dpds.model;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceObjectDPDS {

    @JsonProperty("description")
    private String description;

    @JsonProperty("mediaType")
    private String mediaType;

    @JsonProperty("$ref")
    private String ref;

    @JsonIgnore
    private URI baseUri;
    
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    protected String rawContent;

    public boolean isRef() {
        return ref != null;
    }

    public boolean isResolvedRef() {
        return isRef() && rawContent != null;
    }

    public boolean isInline() {
        return rawContent != null && ref == null;
    }
}
