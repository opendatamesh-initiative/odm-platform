package org.opendatamesh.platform.core.dpds.model.definitions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.net.URI;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiDefinitionReferenceDPDS extends DefinitionReferenceDPDS {
    @JsonProperty("baseUri")
    URI baseUri;

    @JsonProperty("endpoints")
    List<ApiDefinitionEndpointDPDS> endpoints;
}
