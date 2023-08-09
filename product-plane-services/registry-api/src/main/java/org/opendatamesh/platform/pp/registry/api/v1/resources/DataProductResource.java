package org.opendatamesh.platform.pp.registry.api.v1.resources;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProductResource {

    @JsonProperty("id")
    private String id;

    @JsonProperty("fullyQualifiedName")
    private String fullyQualifiedName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("domain")
    private String domain;

    public String toEventString() throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;
        return objectMapper.writeValueAsString(this);
    }
}
