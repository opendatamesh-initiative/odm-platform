package org.opendatamesh.platform.core.dpds.model.core;

import java.net.URI;
import java.net.URISyntaxException;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;



@Data
@EqualsAndHashCode(callSuper=true)
@JsonIgnoreProperties(ignoreUnknown = true)
public abstract class ReferenceableEntityDPDS extends EntityDPDS {
   
    @JsonProperty("$ref")
    protected String ref;

    @JsonIgnore
    protected URI baseUri;

    @JsonIgnore
    protected String originalRef;

    @JsonIgnore
    public URI getRefUri() throws URISyntaxException {
        return new URI(ref).normalize();
    }


    @JsonIgnore
    public boolean isReference() {
        return ref != null;
    }

    @JsonIgnore
    public boolean isInternalReference() {
        return isReference() &&  ref.startsWith("#");
    }

    public boolean isExternalReference() {
        return isReference() &&  !ref.startsWith("#");
    }

    @JsonIgnore
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

    @JsonIgnore
    public String getInternalReferenceComponentName() {
        return isInternalReference()? ref.substring(ref.lastIndexOf("/")+1): null;
    }

}
