package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalComponentsDPDS extends ComponentContainerDPDS{

    @JsonProperty("applicationComponents")
    private List<ApplicationComponentDPDS> applicationComponents = new ArrayList<ApplicationComponentDPDS>();

    @JsonProperty("infrastructuralComponents")
    private List<InfrastructuralComponentDPDS> infrastructuralComponents = new ArrayList<InfrastructuralComponentDPDS>();

    @JsonProperty("lifecycleInfo") 
    private LifecycleInfoDPDS lifecycleInfo;

    public boolean hasLifecycleInfo() {
        return lifecycleInfo != null;
    }
}
