package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opendatamesh.platform.pp.policy.api.resources.utils.TimestampedResource;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyResource extends TimestampedResource {
    private Long id;
    private Long rootId;
    private String name;
    private String displayName;
    private String description;
    private Boolean blockingFlag;
    private String rawContent;
    private String suite;
    private Boolean isLastVersion;
    //TODO policy engine ref
}
