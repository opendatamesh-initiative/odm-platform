package org.opendatamesh.platform.pp.policy.api.resources.events;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.opendatamesh.platform.pp.devops.api.resources.ActivityResource;
import org.opendatamesh.platform.up.executor.api.resources.TaskResource;

public class TaskResultEventTypeResource {

    @JsonProperty("activity")
    ActivityResource activity = null;

    @JsonProperty("task")
    TaskResource task = new TaskResource();

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
