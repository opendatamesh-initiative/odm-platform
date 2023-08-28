package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
//@JsonIgnoreProperties(value={ "id", "entityType" }, allowGetters=true, ignoreUnknown = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentDPDS implements Cloneable {
   
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
    
    @JsonProperty("componentGroup")
    protected String componentGroup;

    @JsonProperty("$ref")
    protected String ref;

    // when the ref is resolved the original location of the actual content is saved here
    // This field is impoirtant to properly manage relative ref in recursive resolvig procedures 
    @JsonProperty("$originalRef")
    protected String originalRef;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    protected String rawContent;

    @JsonAnySetter
    public void ignored(String name, Object value) {
        //System.out.println(name + " : " + value + " : " + value.getClass().getName());
    }
}
