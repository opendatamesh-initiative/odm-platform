package org.opendatamesh.platform.core.dpds.model.core;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.opendatamesh.platform.core.dpds.model.interfaces.PortDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.ApplicationComponentDPDS;
import org.opendatamesh.platform.core.dpds.model.internals.InfrastructuralComponentDPDS;

import java.util.HashMap;
import java.util.Map;


@Data
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "inputPorts", "outputPorts",  "discoveryPorts", "observabilityPorts", "controlPorts", "applicationComponents", "infrastructuralComponents"})
public class ComponentsDPDS extends ComponentContainerDPDS {

    @JsonProperty("inputPorts")
    @Schema(description = "Key-value list of input ports of the Component")
    private Map<String, PortDPDS> inputPorts = new HashMap<>();

    @JsonProperty("outputPorts")
    @Schema(description = "Key-value list of output ports of the Component")
    private Map<String, PortDPDS> outputPorts = new HashMap<>();

    @JsonProperty("discoveryPorts")
    @Schema(description = "Key-value list of discovery ports of the Component")
    private Map<String, PortDPDS> discoveryPorts = new HashMap<>();

    @JsonProperty("observabilityPorts")
    @Schema(description = "Key-value list of observability ports of the Component")
    private Map<String, PortDPDS> observabilityPorts = new HashMap<>();

    @JsonProperty("controlPorts")
    @Schema(description = "Key-value list of control ports of the Component")
    private Map<String, PortDPDS> controlPorts = new HashMap<>();

    @JsonProperty("applicationComponents")
    @Schema(description = "Key-value list of Application Components of the Component")
    private Map<String, ApplicationComponentDPDS> applicationComponents = new HashMap<>();

    @JsonProperty("infrastructuralComponents")
    @Schema(description = "Key-value list of Application Components of the Component")
    private Map<String, InfrastructuralComponentDPDS> infrastructuralComponents = new HashMap<>();

    @JsonProperty("apis")
    @Schema(description = "Key-value list of APIs Standard Definitions of the Component")
    private Map<String, StandardDefinitionDPDS> apis = new HashMap<>();

    @JsonProperty("templates")
    @Schema(description = "Key-value list of template Standard Definitions of the Component")
    private Map<String, StandardDefinitionDPDS> templates = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <E extends ComponentDPDS> Map<String, E> getComponentsByEntityType(EntityTypeDPDS type) {
        switch (type) {
            case INPUTPORT:
                return (Map<String, E>) inputPorts;
            case OUTPUTPORT:
                return (Map<String, E>) outputPorts;
            case DISCOVERYPORT:
                return (Map<String, E>) discoveryPorts;
            case CONTROLPORT:
                return (Map<String, E>) controlPorts;
            case OBSERVABILITYPORT:
                return (Map<String, E>) observabilityPorts;
            case APPLICATION:
                return (Map<String, E>) applicationComponents;
            case INFRASTRUCTURE:
                return (Map<String, E>) infrastructuralComponents;
            case API:
                return (Map<String, E>) apis;
            case TEMPLATE:
                return (Map<String, E>) templates;
            default:
                return null;
        }
    }
}
