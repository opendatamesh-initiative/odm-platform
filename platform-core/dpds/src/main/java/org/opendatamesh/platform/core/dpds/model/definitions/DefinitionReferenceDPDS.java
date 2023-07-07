package org.opendatamesh.platform.core.dpds.model.definitions;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.opendatamesh.platform.core.dpds.model.ReferenceObjectDPDS;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefinitionReferenceDPDS extends ReferenceObjectDPDS {

}
