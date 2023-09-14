package org.opendatamesh.platform.core.dpds.model.definitions;


import org.opendatamesh.platform.core.dpds.model.core.ReferenceObjectDPDS;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Data
@EqualsAndHashCode(callSuper=true)
@ToString(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DefinitionReferenceDPDS extends ReferenceObjectDPDS {

}
