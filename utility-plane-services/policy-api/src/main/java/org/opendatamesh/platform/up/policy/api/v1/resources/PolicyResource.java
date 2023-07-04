package org.opendatamesh.platform.up.policy.api.v1.resources;

import lombok.Data;

import java.util.Date;

@Data
public class PolicyResource {

  private String id;

  private String displayName;

  private String description;
  
  private String rawPolicy;

  private Date createdAt;

  private Date updatedAt;

}