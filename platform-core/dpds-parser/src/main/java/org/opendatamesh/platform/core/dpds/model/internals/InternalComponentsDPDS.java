package org.opendatamesh.platform.core.dpds.model.internals;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.core.ComponentContainerDPDS;

@Data
@EqualsAndHashCode(callSuper=false)
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class InternalComponentsDPDS extends ComponentContainerDPDS{

    @JsonProperty("applicationComponents")
    @Schema(description = "List of the Application Component of the Internal Components")
    private List<ApplicationComponentDPDS> applicationComponents = new ArrayList<ApplicationComponentDPDS>();

    @JsonProperty("infrastructuralComponents")
    @Schema(description = "List of the Infrastructural Component of the Internal Components")
    private List<InfrastructuralComponentDPDS> infrastructuralComponents = new ArrayList<InfrastructuralComponentDPDS>();

    @JsonProperty("lifecycleInfo")
    @Schema(description = "Lifecycle Info object of the Internal Components")
    private LifecycleInfoDPDS lifecycleInfo;

    public boolean hasLifecycleInfo() {
        return lifecycleInfo != null;
    }
}
