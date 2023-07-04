package org.opendatamesh.platform.up.policy.api.v1.resources;

import lombok.Data;

@Data
public class ValidatedPolicyResource {

  private Object validationResult;

  private String policy;
  
}