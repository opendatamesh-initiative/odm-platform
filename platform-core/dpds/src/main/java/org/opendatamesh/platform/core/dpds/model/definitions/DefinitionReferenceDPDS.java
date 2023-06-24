package org.opendatamesh.platform.core.dpds.model.definitions;


import org.opendatamesh.platform.core.dpds.model.ReferenceObjectDPDS;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefinitionReferenceDPDS extends ReferenceObjectDPDS {

}
