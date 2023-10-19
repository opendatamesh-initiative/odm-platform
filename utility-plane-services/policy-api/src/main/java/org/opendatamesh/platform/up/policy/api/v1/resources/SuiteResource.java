package org.opendatamesh.platform.up.policy.api.v1.resources;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.ElementCollection;
import java.util.Date;
import java.util.Set;

@Data
public class SuiteResource {

  @Schema(description = "Auto generated Suite ID")
  private String id;

  @Schema(description = "Suite name to display", required = true)
  private String displayName;

  @Schema(description = "Suite description")
  private String description;

  @Schema(description = "Suite creation timestamp")
  private Date createdAt;

  @Schema(description = "Suite update timestamp")
  private Date updatedAt;

  @ElementCollection
  @Schema(description = "Set of policies inside the Suite")
  private Set<String> policies;

}