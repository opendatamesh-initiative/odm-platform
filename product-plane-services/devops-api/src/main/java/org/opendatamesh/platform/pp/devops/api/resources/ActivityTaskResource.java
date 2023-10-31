package org.opendatamesh.platform.pp.devops.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
@Schema(name = "Task")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityTaskResource  {
    
    @JsonProperty("id")
    @JsonPropertyDescription("Task identifier")
    @Schema(description = "Auto generated Task ID")
    Long id;   

    @JsonProperty("activityId")
    @JsonPropertyDescription("Identifier of the activity to which the task is associated")
    @Schema(description = "ID of the parent Activity", required = true)
    String activityId; 

    @JsonProperty("executorRef")
    @JsonPropertyDescription("Logical name of the target task executor service (ex. `azure-devops`)")
    @Schema(description = "Logical name of the target task executor service", example = "azure-devops")
    String executorRef; 

    @JsonProperty("template")
    @JsonPropertyDescription("The formal description of tasks used by the executor when executing task (i.e. task as a code)")
    @Schema(title="The formal description of tasks used by the executor when executing task (i.e. task as a code)")
    String template;  

    @JsonProperty("configurations")
    @JsonPropertyDescription("Configuration properties used by the executor when executing task. Properties can be used to valorize parameters in template or to modify the execution behaviour of the executor")
    @Schema(title="Configuration properties used by the executor when executing task")
    String configurations;

    @JsonProperty("status")
    @JsonPropertyDescription("The status of the task")
    @Schema(description = "Status of the task")
    ActivityTaskStatus status;

    @JsonProperty("results")
    @JsonPropertyDescription("The output returned by the executor after task succesfully execution")
    Map<String, Object> results;

    @JsonProperty("errors")
    @JsonPropertyDescription("The output returned by the executor after task unsucesfully execution")
    @Schema(description = "The output returned by the executor after task unsucesfully execution")
    String errors;

    @JsonProperty("createdAt")
    @JsonPropertyDescription("Task creation time")
    @Schema(description = "Task creation time")
    private Date createdAt;

    @JsonProperty("startedAt")
    @JsonPropertyDescription("Task start time. Not valorized if the task has not been started yet")
    @Schema(description = "Task start time. Not valorized if the task has not been started yet")
    private Date startedAt;

    @JsonProperty("finishedAt")
    @JsonPropertyDescription("Task finished time. Not valorized if the task has not been finished yet")
    @Schema(description = "Task finished time. Not valorized if the task has not been started yet")
    private Date finishedAt;
}
