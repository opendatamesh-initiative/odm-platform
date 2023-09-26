package org.opendatamesh.platform.core.dpds.model.info;

import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
//@JsonIgnoreProperties(value={ "id", "entityType" }, allowGetters=true, ignoreUnknown = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InfoDPDS implements Cloneable {

    @JsonProperty("id")
    @Schema(description = "Data Product ID pf the Info object", required = true)
    private String dataProductId;
    
    @JsonProperty("fullyQualifiedName")
    @Schema(description = "Info object fully qualified name", required = true)
    private String fullyQualifiedName;

    @JsonProperty("entityType")
    @Schema(description = "Info object entity type", required = true)
    private String entityType;

    @JsonProperty("name")
    @Schema(description = "Info object name", required = true)
    private String name;

    @JsonProperty("version")
    @Schema(description = "Info object version", required = true)
    private String versionNumber;

    @JsonProperty("displayName")
    @Schema(description = "Info object name to display", required = true)
    private String displayName;

    @JsonProperty("description")
    @Schema(description = "Info object description", required = true)
    private String description;

    @JsonProperty("domain")
    @Schema(description = "Info object domain", required = true)
    private String domain;

    @JsonProperty("owner")
    @Schema(description = "Owner object of the Info object", required = true)
    private OwnerDPDS owner;

    @JsonProperty("contactPoints")
    @Schema(description = "List of Contact Point objects of the Info object", required = true)
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
