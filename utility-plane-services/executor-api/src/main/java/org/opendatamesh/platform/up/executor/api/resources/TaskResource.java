package org.opendatamesh.platform.up.executor.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResource  {
    
    @JsonProperty("id")
    @Schema(description = "Auto generated Task ID")
    Long id;   

    @JsonProperty("activityId")
    @Schema(description = "ID of the parent Activity of the Task")
    String activityId; 

    @JsonIgnore
    @Schema(description = "Logical name of the target task executor service", example = "azure-devops")
    String executorRef; 

    @JsonProperty("callbackRef")
    @Schema(description = "Reference for the callback from the executor service")
    String callbackRef;
    
    @JsonProperty("template")
    @Schema(description = "Template of the Task")
    String template;

    @JsonProperty("configurations")
    @Schema(description = "Configurations for the Task")
    String configurations;

    @JsonProperty("status")
    @Schema(description = "Task status")
    TaskStatus status;

    @JsonProperty("results")
    @Schema(description = "Task results in case of successful execution")
    Map<String, Object> results;

    @JsonProperty("errors")
    @Schema(description = "Task results in case of failures")
    String errors;

    @JsonProperty("createdAt")
    @Schema(description = "Creation timestamp of the Task")
    private Date createdAt;

    @JsonProperty("startedAt")
    @Schema(description = "Timestamp of the start of the Task execution")
    private Date startedAt;

    @JsonProperty("finishedAt")
    @Schema(description = "Timestamp of the end of the Task execution")
    private Date finishedAt;
}
