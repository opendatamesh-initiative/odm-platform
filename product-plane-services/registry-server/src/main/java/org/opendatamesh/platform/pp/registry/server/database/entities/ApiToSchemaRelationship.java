package org.opendatamesh.platform.pp.registry.server.database.entities;

import lombok.Data;

import javax.persistence.*;
import java.beans.Transient;
import java.io.Serializable;

@Data
@Entity(name = "ApiToSchemaRelationship")
@Table(name = "REL_APIS_TO_SCHEMAS", schema="ODMREGISTRY")
public class ApiToSchemaRelationship {
    
    @EmbeddedId
    private ApiToSchemaRelationshipId id;

    @Column(name = "OPERATION_ID")
    String operationId;

    @Column(name = "OUTPUT_MEDIA_TYPE")
    String outputMediaType;

    @Transient
    public String getApiId() {
        return (id == null ? null: id.getApiId());
    }

    public void setApiId(String apiId) {
        if(id == null) id = new ApiToSchemaRelationshipId();
        id.setApiId(apiId);
    }

    @Transient
    public Long getSchemaId() {
        return (id == null ? null: id.getSchemaId());
    }

    public void setSchemaId(Long schemaId) {
        if(id == null) id = new ApiToSchemaRelationshipId();
        id.setSchemaId(schemaId);
    }

    @Embeddable
    @Data
    public static class ApiToSchemaRelationshipId implements Serializable {
        @Column(name = "API_ID")
        String apiId;
        
        @Column(name = "SCHEMA_ID")
        Long schemaId;

        public ApiToSchemaRelationshipId() {           
        }

        public ApiToSchemaRelationshipId(String apiId, Long schemaId) {
            this.apiId = apiId;
            this.schemaId = schemaId;
        }
    }
   
}
