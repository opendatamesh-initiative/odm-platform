package org.opendatamesh.platform.up.policy.api.v1.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.ElementCollection;
import java.util.List;

@Data
public class ValidatedSuiteResource {

  @ElementCollection
  @Schema(description = "List of Validated Policies inside the Suite")
  private List<ValidatedPolicyResource> validatedPolicies;

  @Schema(description = "Suite name")
  private String suite;
  
}