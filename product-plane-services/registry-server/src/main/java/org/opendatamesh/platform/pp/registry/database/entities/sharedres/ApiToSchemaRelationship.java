package org.opendatamesh.platform.pp.registry.database.entities.sharedres;

import java.beans.Transient;
import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;

import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@Entity(name = "ApiToSchemaRelationship")
@Table(name = "DPDS_API_TO_SCHEMA_RELATIONSHIPS", schema="PUBLIC")
public class ApiToSchemaRelationship {
    
    @EmbeddedId
    private ApiToSchemaRelationshipId id;

    @Column(name = "OPERATION_ID")
    String operationId;

    @Column(name = "OUTPUT_MEDIA_TYPE")
    String outputMediaType;

    @Transient
    public Long getApiId() {
        return (id == null ? null: id.getApiId());
    }

    public void setApiId(Long apiId) {
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
        Long apiId;
        
        @Column(name = "SCHEMA_ID")
        Long schemaId;

        public ApiToSchemaRelationshipId() {           
        }

        public ApiToSchemaRelationshipId(Long apiId, Long schemaId) {
            this.apiId = apiId;
            this.schemaId = schemaId;
        }
    }
   
}
