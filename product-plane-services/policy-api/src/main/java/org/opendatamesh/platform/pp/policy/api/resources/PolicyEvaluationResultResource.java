package org.opendatamesh.platform.pp.policy.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opendatamesh.platform.pp.policy.api.resources.utils.TimestampedResource;

@Data
@EqualsAndHashCode(callSuper = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyEvaluationResultResource extends TimestampedResource {
    private Long id;
    private String dataProductVersion;
    private String dataProductId;
    private String inputObject;
    private String outputObject;
    private Boolean result;
    private Long policyId;
}
