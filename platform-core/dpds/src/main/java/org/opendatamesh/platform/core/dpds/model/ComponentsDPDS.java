package org.opendatamesh.platform.core.dpds.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.opendatamesh.platform.core.dpds.ObjectMapperFactory;

@Data
@EqualsAndHashCode(callSuper=false)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({ "inputPorts", "outputPorts",  "discoveryPorts", "observabilityPorts", "controlPorts", "applicationComponents", "infrastructuralComponents"})
public class ComponentsDPDS extends ComponentContainerDPDS {

    @JsonProperty("inputPorts")
    private Map<String, PortDPDS> inputPorts = new HashMap<>();

    @JsonProperty("outputPorts")
    private Map<String, PortDPDS> outputPorts = new HashMap<>();

    @JsonProperty("discoveryPorts")
    private Map<String, PortDPDS> discoveryPorts = new HashMap<>();

    @JsonProperty("observabilityPorts")
    private Map<String, PortDPDS> observabilityPorts = new HashMap<>();

    @JsonProperty("controlPorts")
    private Map<String, PortDPDS> controlPorts = new HashMap<>();

    @JsonProperty("applicationComponents")
    private Map<String, ApplicationComponentDPDS> applicationComponents = new HashMap<>();

    @JsonProperty("infrastructuralComponents")
    private Map<String, InfrastructuralComponentDPDS> infrastructuralComponents = new HashMap<>();

    @JsonProperty("apis")
    private Map<String, StandardDefinitionDPDS> apis = new HashMap<>();

    @JsonProperty("templates")
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
