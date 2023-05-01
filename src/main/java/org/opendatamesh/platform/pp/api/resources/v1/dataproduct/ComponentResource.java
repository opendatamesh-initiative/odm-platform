package org.opendatamesh.platform.pp.api.resources.v1.dataproduct;

import lombok.Data;

import java.util.Date;

import org.opendatamesh.platform.pp.api.enums.EntityType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;


@Data
@JsonIgnoreProperties(value={ "id", "entityType" }, allowGetters=true, ignoreUnknown = true)
public class ComponentResource {
   
    @JsonProperty("id")
    protected String id;
    
    @JsonProperty("fullyQualifiedName")
    protected String fullyQualifiedName;
    
    @JsonProperty("entityType")
    protected EntityType entityType;
   
    @JsonProperty("name")
    protected String name;
    
    @JsonProperty("version")
    protected String version;
   
    @JsonProperty("displayName")
    protected String displayName;
    
    @JsonProperty("description")
    protected String description;
    
    @JsonProperty("componentGroup")
    protected String componentGroup;

    //field to declare the component with a reference
    @JsonProperty("$ref")
    protected String ref;

    @JsonIgnore
    protected String rawContent;

    @JsonIgnore
    protected Date createdAt;

    @JsonIgnore
    protected Date updatedAt;
}
