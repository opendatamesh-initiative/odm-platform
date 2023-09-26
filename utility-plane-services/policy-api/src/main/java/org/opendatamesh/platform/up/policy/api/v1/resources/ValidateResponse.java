package org.opendatamesh.platform.up.policy.api.v1.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.ElementCollection;
import java.util.List;

@Data
public class ValidateResponse {

  @ElementCollection
  @Schema(description = "List of Validated Policies")
  private List<ValidatedPolicyResource> validatedPolicies;

  @ElementCollection
  @Schema(description = "List of Validated Suites")
  private List<ValidatedSuiteResource> validatedSuites;
  
}