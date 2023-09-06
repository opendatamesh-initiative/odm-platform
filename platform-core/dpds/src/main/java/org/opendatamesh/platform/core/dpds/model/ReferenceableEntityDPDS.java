package org.opendatamesh.platform.core.dpds.model;

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

    // when the ref is resolved the original location of the actual content is saved here
    // This field is impoirtant to properly manage relative ref in recursive resolvig procedures 
    @JsonProperty("$originalRef")
    protected String originalRef;
}
