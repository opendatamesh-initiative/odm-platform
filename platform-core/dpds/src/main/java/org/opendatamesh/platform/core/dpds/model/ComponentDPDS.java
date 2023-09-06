package org.opendatamesh.platform.core.dpds.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
//@JsonIgnoreProperties(value={ "id", "entityType" }, allowGetters=true, ignoreUnknown = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ComponentDPDS extends ReferenceableEntityDPDS {
       
    

    @JsonProperty("componentGroup")
    protected String componentGroup;

    @JsonIgnore
    @EqualsAndHashCode.Exclude
    protected String rawContent;

    @JsonProperty("tags")
    private List<String> tags = new ArrayList<String>();

    @JsonProperty("externalDocs")
    private ExternalResourceDPDS externalDocs;

    public boolean isReference() {
        return ref != null;
    }

    public boolean isInternalReference() {
        return isReference() &&  ref.startsWith("#");
    }

    public String getInternalReferenceGroupName() {
        String entityType = null;
        if(isInternalReference()) {
            String[] refTokens = ref.split("/");
            if(refTokens.length > 1) {
                entityType = refTokens[1];
            }
        }
        return entityType;
    }

    public String getInternalReferenceComponentName() {
        return isInternalReference()? ref.substring(ref.lastIndexOf("/")+1): null;
    }

    public boolean isExternalReference() {
        return isReference() &&  !ref.startsWith("#");
    }



}
