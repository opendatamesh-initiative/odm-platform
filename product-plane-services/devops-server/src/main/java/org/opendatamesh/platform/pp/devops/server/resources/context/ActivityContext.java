package org.opendatamesh.platform.pp.devops.server.resources.context;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;
import java.util.Map;

@Data
public class ActivityContext {

    @JsonProperty("status")
    private ActivityResultStatus status;

    @JsonProperty("finishedAt")
    private Date finishedAt;

    @JsonProperty("results")
    private Map<String, Object> results;

}
