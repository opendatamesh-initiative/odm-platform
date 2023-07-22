package org.opendatamesh.platform.core.dpds.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class LifecycleInfoDPDS {
    Map<String, ActivityInfoDPDS> stages;

    @JsonIgnore
    protected String rawContent;

    public LifecycleInfoDPDS() {
        stages = new HashMap<String, ActivityInfoDPDS>();
    }
}
