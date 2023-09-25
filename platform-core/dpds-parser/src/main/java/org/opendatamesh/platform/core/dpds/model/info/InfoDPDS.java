package org.opendatamesh.platform.core.dpds.model.info;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
//@JsonIgnoreProperties(value={ "id", "entityType" }, allowGetters=true, ignoreUnknown = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoDPDS implements Cloneable {

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
    private OwnerDPDS owner;

    @JsonProperty("contactPoints")
    private List<ContactPointDPDS> contactPoints = new ArrayList<>();

    @JsonAnySetter
    public void ignored(String name, Object value) {
        System.out.println(name + " : " + value + " : " + value.getClass().getName());
    }

    public InfoDPDS() {
    }

    public InfoDPDS(String fullyQualifiedName) {
        this.fullyQualifiedName = fullyQualifiedName;
    }

    public InfoDPDS clone() throws CloneNotSupportedException
    {
        return (InfoDPDS) super.clone();
    }
}
