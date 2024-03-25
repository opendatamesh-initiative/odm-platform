package org.opendatamesh.platform.pp.policy.api.resources.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskResultEventTypeResource {

    @JsonProperty("activity")
    ActivityResource activity;

    @JsonProperty("task")
    TaskResource task;

    public ActivityResource getActivity() {
        return activity;
    }

    public void setActivity(ActivityResource activity) {
        this.activity = activity;
    }

    public TaskResource getTask() {
        return task;
    }

    public void setTask(TaskResource task) {
        this.task = task;
    }

}
