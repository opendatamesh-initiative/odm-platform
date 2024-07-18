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
public class OwnerResource {

    @JsonProperty("id")
    @Schema(description = "Auto generated Owner ID")
    private String id;

    @JsonProperty("name")
    @Schema(description = "Owner name", required = true)
    private String name;

    public String toEventString() throws JsonProcessingException {
        ObjectMapper objectMapper = ObjectMapperFactory.JSON_MAPPER;
        return objectMapper.writeValueAsString(this);
    }
}
