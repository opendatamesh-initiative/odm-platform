package org.opendatamesh.platform.pp.registry.resources.v1;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiToSchemaRelationshipResource {

 
    @JsonIgnore
    private ApiToSchemaRelationshipId id;

    @JsonProperty("operationId")
    String operationId;

    @JsonProperty("outputMediaType")
    String outputMediaType;

    @JsonProperty("apiId")
    public Long getApiId() {
        return (id == null ? null: id.getApiId());
    }

    public void setApiId(Long apiId) {
        if(id == null) id = new ApiToSchemaRelationshipId();
        id.setApiId(apiId);
    }

    @JsonProperty("schemaId")
    public Long getSchemaId() {
        return (id == null ? null: id.getSchemaId());
    }

    public void setSchemaId(Long schemaId) {
        if(id == null) id = new ApiToSchemaRelationshipId();
        id.setSchemaId(schemaId);
    }

    @Data
    public static class ApiToSchemaRelationshipId implements Serializable {
        @JsonProperty("operationId")
        Long apiId;
        
        @JsonProperty("operationId")
        Long schemaId;

        public ApiToSchemaRelationshipId() {           
        }

        public ApiToSchemaRelationshipId(Long apiId, Long schemaId) {
            this.apiId = apiId;
            this.schemaId = schemaId;
        }
    }
}
