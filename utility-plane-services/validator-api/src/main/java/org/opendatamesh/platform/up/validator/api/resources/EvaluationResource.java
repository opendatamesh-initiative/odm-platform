package org.opendatamesh.platform.up.validator.api.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class EvaluationResource {

  @Schema(description = "Policy Evaluation ID to reconcile the evaluation result with the triggering request")
  private Long policyEvaluationId;

  @Schema(description = "Synthetic results stating if the document is valid or not against the provided policy")
  private Boolean evaluationResult;

  @Schema(description = "Extended result of the evaluation")
  private Object outputObject;

}