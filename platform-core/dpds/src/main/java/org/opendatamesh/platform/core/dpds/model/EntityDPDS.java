package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class EntityDPDS implements Cloneable {
   
    @JsonProperty("id")
    protected String id;
    
    @JsonProperty("fullyQualifiedName")
    protected String fullyQualifiedName;
    
    @JsonProperty("entityType")
    private String entityType;
   
    @JsonProperty("name")
    protected String name;
    
    @JsonProperty("version")
    protected String version;
   
    @JsonProperty("displayName")
    protected String displayName;
    
    @JsonProperty("description")
    protected String description;
    
   

   
    @JsonAnySetter
    public void ignored(String name, Object value) {
        //System.out.println(name + " : " + value + " : " + value.getClass().getName());
    }
}