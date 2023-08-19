package org.opendatamesh.platform.pp.devops.api.resources;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperty;
import lombok.Data;

import java.util.Date;

@Data
@Schema(name = "Task")
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActivityTaskResource  {
    
    @JsonProperty("id")
    @JsonPropertyDescription("Task identifier")
    Long id;   

    @JsonProperty("activityId")
    @JsonPropertyDescription("Identifier of the activity to which the task is associated")
    String activityId; 

    @JsonProperty("executorRef")
    @JsonPropertyDescription("Logical name of the target task executor service (ex. `azure-devops`)")
    String executorRef; 
    
    @Schema(title="The formal description of tasks used by the executor when executing task (i.e. task as a code)")
    @JsonProperty("template")
    @JsonPropertyDescription("The formal description of tasks used by the executor when executing task (i.e. task as a code)")
    String template;  

    @JsonProperty("configurations")
    @JsonPropertyDescription("Configuration properties used by the executor when executing task. Properties can be used to valorize parameters in template or to modify the execution behaviour of the executor")
    String configurations;  

    @JsonProperty("status")
    @JsonPropertyDescription("The status of the task")
    ActivityTaskStatus status;

    @JsonProperty("results")
    @JsonPropertyDescription("The output returned by the executor after task succesfully execution")
    String results;

    @JsonProperty("errors")
    @JsonPropertyDescription("The output returned by the executor after task unsucesfully execution")
    String errors;

    @JsonProperty("createdAt")
    @JsonPropertyDescription("Task creation time")
    private Date createdAt;

    @JsonProperty("startedAt")
    @JsonPropertyDescription("Task start time. Not valorized if the task has not been started yet")
    private Date startedAt;

    @JsonProperty("finishedAt")
    @JsonPropertyDescription("Task finished time. Not valorized if the task has not been finished yet")
    private Date finishedAt;
}
