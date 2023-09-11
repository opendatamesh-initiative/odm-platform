package org.opendatamesh.platform.pp.registry.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.EntityTypeDPDS;
import org.opendatamesh.platform.pp.registry.api.parser.DomainIdentifierStrategy;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DomainResource {

    @JsonProperty("id")
    private String id;

    @JsonProperty("fullyQualifiedName")
    private String fullyQualifiedName;

    @JsonProperty("entityType")
    private EntityTypeDPDS entityType; //Always "domain"

    @JsonProperty("name")
    private String name;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("summary")
    private String summary;

    @JsonProperty("description")
    private String description;

    public String toEventString() throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;
        return objectMapper.writeValueAsString(this);
    }
}
