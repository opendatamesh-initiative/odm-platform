package org.opendatamesh.platform.up.policy.api.v1.resources;

import lombok.Data;

import javax.persistence.ElementCollection;
import java.util.Date;
import java.util.Set;

@Data
public class SuiteResource {

  private String id;

  private String displayName;

  private String description;

  private Date createdAt;

  private Date updatedAt;

  @ElementCollection
  private Set<String> policies;

}