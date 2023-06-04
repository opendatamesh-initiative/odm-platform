package org.opendatamesh.platform.up.notification.api.v1.resources;


import java.util.Date;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EventResource {
    @JsonProperty("id")
    Long id;

    @JsonProperty("type")
    private String type;

    @JsonProperty("entityId")
    private String entityId;

    @JsonProperty("beforeState")
    private String beforeState;

    @JsonProperty("afterState")
    private String afterState;

    @JsonProperty("time")
    private Date time;
}