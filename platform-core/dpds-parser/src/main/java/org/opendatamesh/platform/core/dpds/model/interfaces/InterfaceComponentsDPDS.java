package org.opendatamesh.platform.core.dpds.model.interfaces;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.*;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;
import org.opendatamesh.platform.core.dpds.model.core.ComponentContainerDPDS;
import org.opendatamesh.platform.core.dpds.model.core.EntityTypeDPDS;

@Data
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "inputPorts", "outputPorts",  "discoveryPorts", "observabilityPorts", "controlPorts"})
public class InterfaceComponentsDPDS extends ComponentContainerDPDS{

    @JsonProperty("inputPorts")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "List of the input Ports of the Interface Components", required = true)
    private List<PortDPDS> inputPorts = new ArrayList<PortDPDS>();

    @JsonProperty("outputPorts")
    @Schema(description = "List of the output Ports of the Interface Components", required = true)
    private List<PortDPDS> outputPorts = new ArrayList<PortDPDS>();

    @JsonProperty("discoveryPorts")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "List of the discovery Ports of the Interface Components", required = true)
    private List<PortDPDS> discoveryPorts = new ArrayList<PortDPDS>();

    @JsonProperty("observabilityPorts")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "List of the observability Ports of the Interface Components", required = true)
    private List<PortDPDS> observabilityPorts = new ArrayList<PortDPDS>();

    @JsonProperty("controlPorts")
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    @Schema(description = "List of the control Ports of the Interface Components", required = true)
    private List<PortDPDS> controlPorts = new ArrayList<PortDPDS>();

    public boolean hasPorts(EntityTypeDPDS entityType) {
        List<PortDPDS> ports = getPortListByEntityType(entityType);
        return ports != null && !ports.isEmpty();
    }

    public List<PortDPDS> getPortListByEntityType(EntityTypeDPDS entityType) {
        switch (entityType) {
            case OUTPUTPORT:
                return outputPorts;
            case INPUTPORT:
                return inputPorts;
            case CONTROLPORT:
                return controlPorts;
            case DISCOVERYPORT:
                return discoveryPorts;
            case OBSERVABILITYPORT:
                return observabilityPorts;
            default:
                return null;
        }
    }
}
