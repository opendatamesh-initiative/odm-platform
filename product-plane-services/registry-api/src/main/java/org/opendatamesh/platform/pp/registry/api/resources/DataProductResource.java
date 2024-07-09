package org.opendatamesh.platform.pp.registry.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.opendatamesh.platform.core.commons.ObjectMapperFactory;;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataProductResource {

    @JsonProperty("id")
    @Schema(description = "Auto generated DataProduct ID")
    private String id;

    @JsonProperty("fullyQualifiedName")
    @Schema(description = "DataProduct fully qualified name", required = true)
    private String fullyQualifiedName;

    @JsonProperty("description")
    @Schema(description = "DataProduct description", required = true)
    private String description;

    @JsonProperty("domain")
    @Schema(description = "DataProduct domain", required = true)
    private String domain;

    public String toEventString() throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;
        return objectMapper.writeValueAsString(this);
    }
}
