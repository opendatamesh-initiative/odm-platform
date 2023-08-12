package org.opendatamesh.platform.pp.devops.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityTaskResource  {
    
    @JsonProperty("id")
    Long id;   

    @JsonProperty("activityId")
    String activityId; 

    @JsonProperty("executorRef")
    String executorRef; 
    
    @JsonProperty("template")
    String template;  

    @JsonProperty("configurations")
    String configurations;  

    @JsonProperty("status")
    ActivityTaskStatus status;

    @JsonProperty("results")
    String results;

    @JsonProperty("errors")
    String errors;

    @JsonProperty("createdAt")
    private Date createdAt;

    @JsonProperty("startedAt")
    private Date startedAt;

    @JsonProperty("finishedAt")
    private Date finishedAt;
}
