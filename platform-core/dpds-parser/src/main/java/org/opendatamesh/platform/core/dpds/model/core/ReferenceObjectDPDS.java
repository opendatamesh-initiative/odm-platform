package org.opendatamesh.platform.core.dpds.model.core;

import java.net.URI;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenceObjectDPDS {

    @JsonProperty("description")
    @Schema(description = "Reference Object description", required = true)
    private String description;

    @JsonProperty("mediaType")
    @Schema(description = "Reference Object Media Type", required = true)
    private String mediaType;

    @JsonProperty("$ref")
    @Schema(description = "Reference Object reference", required = true)
    private String ref;

    @JsonIgnore
    @Schema(description = "Reference Object base URI", required = true)
    private URI baseUri;
    
    @JsonIgnore
    @EqualsAndHashCode.Exclude
    @Schema(description = "Reference Object raw content", required = true)
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
