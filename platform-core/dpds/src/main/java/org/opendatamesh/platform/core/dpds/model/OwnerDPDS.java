package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OwnerDPDS {

    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;
}
