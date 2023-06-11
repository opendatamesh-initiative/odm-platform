package org.opendatamesh.platform.pp.registry.resources.v1.dataproduct;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@JsonIgnoreProperties(value={ "id", "entityType" }, allowGetters=true, ignoreUnknown = true)
public class InfoResource implements Cloneable {

    @JsonProperty("id")
    private String dataProductId;
    
    @JsonProperty("fullyQualifiedName")
    private String fullyQualifiedName;

    @JsonProperty("entityType")
    private String entityType;

    @JsonProperty("name")
    private String name;

    @JsonProperty("version")
    private String versionNumber;

    @JsonProperty("displayName")
    private String displayName;

    @JsonProperty("description")
    private String description;

    @JsonProperty("domain")
    private String domain;

    @JsonProperty("owner")
    private OwnerResource owner;

    @JsonProperty("contactPoints")
    private List<ContactPointResource> contactPoints = new ArrayList<>();

    @JsonAnySetter
    public void ignored(String name, Object value) {
        System.out.println(name + " : " + value + " : " + value.getClass().getName());
    }

    public InfoResource() {
    }

    public InfoResource(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public InfoResource clone() throws CloneNotSupportedException
    {
        return (InfoResource) super.clone();
    }
}
