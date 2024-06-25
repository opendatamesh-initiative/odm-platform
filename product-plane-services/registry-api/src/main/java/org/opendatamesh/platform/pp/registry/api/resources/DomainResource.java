package org.opendatamesh.platform.pp.registry.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.opendatamesh.dpds.model.core.EntityTypeDPDS;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DomainResource {

    @JsonProperty("id")
    @Schema(description = "Auto generated Domain ID")
    private String id;

    @JsonProperty("fullyQualifiedName")
    @Schema(description = "Domain fully qualified name", required = true)
    private String fullyQualifiedName;

    @JsonProperty("entityType")
    @Schema(description = "Entity Type", required = true, defaultValue = "domain")
    private EntityTypeDPDS entityType; //Always "domain"

    @JsonProperty("name")
    @Schema(description = "Domain name", required = true)
    private String name;

    @JsonProperty("displayName")
    @Schema(description = "Domain name to display", required = true)
    private String displayName;

    @JsonProperty("summary")
    @Schema(description = "Domain summary", required = true)
    private String summary;

    @JsonProperty("description")
    @Schema(description = "Domain description", required = true)
    private String description;

    public String toEventString() throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;
        return objectMapper.writeValueAsString(this);
    }
}
