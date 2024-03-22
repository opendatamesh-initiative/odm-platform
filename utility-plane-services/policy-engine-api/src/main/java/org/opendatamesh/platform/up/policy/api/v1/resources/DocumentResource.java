package org.opendatamesh.platform.up.policy.api.v1.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class DocumentResource {

    @Schema(description = "Policy Evaluation ID to reconcile the evaluation result with the triggering request")
    private String policyEvaluationId;

    @Schema(description = "JSON representation of the policy to evaluate against")
    private PolicyResource policy;

    @Schema(description = "JSON representation of the object to be evaluated")
    private Object objectToEvaluate;

}
