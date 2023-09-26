package org.opendatamesh.platform.up.policy.api.v1.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ValidatedPolicyResource {

  @Schema(description = "Result of the validation of the policy given by OPA server")
  private Object validationResult;

  @Schema(description = "Validated Policy")
  private String policy;
  
}