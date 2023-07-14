package org.opendatamesh.platform.up.executor.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResource  {
    
    @JsonProperty("id")
    Long id;   
    
    @JsonProperty("template")
    String template;  

    @JsonProperty("configurations")
    String configurations;  

    @JsonProperty("status")
    TaskStatus status;

    @JsonProperty("results")
    String results;

    @JsonProperty("errors")
    String errors;

    @JsonProperty("startedAt")
    private Date startedAt;

    @JsonProperty("finishedAt")
    private Date finishedAt;
}
