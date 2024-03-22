package org.opendatamesh.platform.up.policy.api.v1.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

@Data
public class PolicyResource {

  @Schema(description = "Auto generated Policy ID")
  private String id;

  @Schema(description = "Policy name to display")
  private String displayName;

  @Schema(description = "Policy description")
  private String description;

  @Schema(description = "Raw content of the Policy")
  private String rawPolicy;

  @Schema(description = "Policy creation timestamp")
  private Date createdAt;

  @Schema(description = "Policy update timestamp")
  private Date updatedAt;

}